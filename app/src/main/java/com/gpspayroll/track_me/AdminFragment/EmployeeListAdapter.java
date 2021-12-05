package com.gpspayroll.track_me.AdminFragment;

import android.content.Context;
import android.content.DialogInterface;
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
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gpspayroll.track_me.Authentication.LoginActivity;
import com.gpspayroll.track_me.ModelClasses.StoreEmployees;
import com.gpspayroll.track_me.R;

import java.util.ArrayList;

public class EmployeeListAdapter extends RecyclerView.Adapter<EmployeeListAdapter.MyViewHolder> {

    Context context;
    ArrayList<StoreEmployees> storeEmployeesList;
    DatabaseReference databaseReference;

    public EmployeeListAdapter(Context c, ArrayList<StoreEmployees> p) {
        context = c;
        storeEmployeesList = p;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.employee_list_adapter, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeListAdapter.MyViewHolder holder, int position) {
        StoreEmployees storeEmployees = storeEmployeesList.get(position);

        String name = storeEmployees.getUsername();
        String checkin = storeEmployees.getCheckin();
        String checkout = storeEmployees.getCheckout();
        String workhour = storeEmployees.getWorkhour();
        String remuneration = storeEmployees.getRemuneration();
        String phone = storeEmployees.getUserPhone();

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
                            databaseReference.child(phone).removeValue();
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
