package com.gpspayroll.track_me.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gpspayroll.track_me.AdminFragment.Payroll;
import com.gpspayroll.track_me.ModelClasses.StoreEmployees;
import com.gpspayroll.track_me.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class OnFieldEmployeeListAdapter extends RecyclerView.Adapter<OnFieldEmployeeListAdapter.MyViewHolder> {

    Context context;
    ArrayList<StoreEmployees> storeEmployeesList;
    DatabaseReference databaseReference;

    public OnFieldEmployeeListAdapter(Context c, ArrayList<StoreEmployees> p) {
        context = c;
        storeEmployeesList = p;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.on_field_employee_list_adapter, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnFieldEmployeeListAdapter.MyViewHolder holder, int position) {
        StoreEmployees storeEmployees = storeEmployeesList.get(position);

        String name = storeEmployees.getUsername();
        String checkin = storeEmployees.getCheckin();
        String checkout = storeEmployees.getCheckout();
        String workhour = storeEmployees.getWorkhour();
        String remuneration = storeEmployees.getRemuneration();
        String phone = storeEmployees.getUserPhone();
        Date cal = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MMM-yyyy");
        String date = simpleDateFormat1.format(cal);

        holder.nameText.setText(name);
        holder.checkinText.setText(checkin);
        holder.checkoutText.setText(checkout);
        holder.workhourText.setText(workhour);
        holder.remunerationText.setText(remuneration + " BDT");

        holder.deleteEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder;
                alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("Do you want to remove this employee ?");
                alertDialogBuilder.setIcon(R.drawable.exit);
                alertDialogBuilder.setCancelable(false);

                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            databaseReference.child(date).child(phone).removeValue();
                            Toast.makeText(context, "Employee Removed", Toast.LENGTH_SHORT).show();
                        } catch (Exception e){
                            Log.i("Error_Db", e.getMessage());
                        }
                    }
                });

                alertDialogBuilder.setNeutralButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        holder.payNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    databaseReference.child(date).child(phone).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            try {
                                if (!snapshot.child("checkout").getValue().toString().equals("Counting")) {
                                    Bundle armgs = new Bundle();
                                    armgs.putString("phone_key", phone);
                                    armgs.putString("name_key", name);
                                    armgs.putString("hours_key", workhour);
                                    armgs.putString("salary_key", remuneration);
                                    armgs.putString("checkIn_key", checkin);
                                    armgs.putString("checkOut_key", checkout);

                                    Payroll payroll = new Payroll();
                                    payroll.setArguments(armgs);

                                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                                    payroll.show(activity.getSupportFragmentManager(), "Sample dialog");

                                } else {
                                    Toast.makeText(context, "Employee is Still Working", Toast.LENGTH_SHORT).show();
                                }

                            } catch (Exception e){
                                Log.i("Db_Error", e.getMessage());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.i("DatabaseError", error.getMessage());
                        }
                    });

                } catch (Exception e){
                    Toast.makeText(context, "Payroll Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return storeEmployeesList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, checkinText, checkoutText, workhourText, remunerationText;
        ImageView deleteEmployee;
        LinearLayout payNow;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            nameText = itemView.findViewById(R.id.employeeNameId);
            checkinText = itemView.findViewById(R.id.checkInRecordId);
            checkoutText = itemView.findViewById(R.id.checkOutRecordId);
            workhourText = itemView.findViewById(R.id.workHourId);
            remunerationText = itemView.findViewById(R.id.remunerationId);

            deleteEmployee = itemView.findViewById(R.id.deleteEmployeeId);
            payNow = itemView.findViewById(R.id.payEmployeeId);

            databaseReference = FirebaseDatabase.getInstance().getReference("Employees List");
        }
    }
}
