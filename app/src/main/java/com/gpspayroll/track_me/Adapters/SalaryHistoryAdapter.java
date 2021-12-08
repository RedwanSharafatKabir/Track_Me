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
import com.gpspayroll.track_me.ModelClasses.StoreSalaryHistory;
import com.gpspayroll.track_me.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SalaryHistoryAdapter extends RecyclerView.Adapter<SalaryHistoryAdapter.MyViewHolder> {

    Context context;
    ArrayList<StoreSalaryHistory> storeSalaryHistoryList;
    DatabaseReference databaseReference;

    public SalaryHistoryAdapter(Context c, ArrayList<StoreSalaryHistory> p) {
        context = c;
        storeSalaryHistoryList = p;
    }

    @NonNull
    @Override
    public SalaryHistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.salary_history_adapter, parent, false);

        return new SalaryHistoryAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalaryHistoryAdapter.MyViewHolder holder, int position) {
        StoreSalaryHistory storeSalaryHistory = storeSalaryHistoryList.get(position);

        String name = storeSalaryHistory.getUsername();
        String checkin = storeSalaryHistory.getCheckin();
        String checkout = storeSalaryHistory.getCheckout();
        String workhour = storeSalaryHistory.getWorkhour();
        String remuneration = storeSalaryHistory.getRemuneration();
        String phone = storeSalaryHistory.getUserPhone();
        String status = storeSalaryHistory.getStatus();

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
                            Toast.makeText(context, "Employee History Removed", Toast.LENGTH_SHORT).show();

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
    }

    @Override
    public int getItemCount() {
        return storeSalaryHistoryList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, checkinText, checkoutText, workhourText, remunerationText;
        ImageView deleteEmployee;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            nameText = itemView.findViewById(R.id.employeeNameHistoryId);
            checkinText = itemView.findViewById(R.id.checkInRecordHistoryId);
            checkoutText = itemView.findViewById(R.id.checkOutRecordHistoryId);
            workhourText = itemView.findViewById(R.id.workHourHistoryId);
            remunerationText = itemView.findViewById(R.id.remunerationHistoryId);

            deleteEmployee = itemView.findViewById(R.id.deleteEmployeeHistoryId);

            databaseReference = FirebaseDatabase.getInstance().getReference("Payment History");
        }
    }
}
