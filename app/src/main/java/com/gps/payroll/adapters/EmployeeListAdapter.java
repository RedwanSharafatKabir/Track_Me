package com.gps.payroll.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gps.payroll.modelClasses.StoreEmployeeData;
import com.gps.payroll.R;

import java.util.ArrayList;

public class EmployeeListAdapter extends RecyclerView.Adapter<EmployeeListAdapter.MyViewHolder> {

    Context context;
    ArrayList<StoreEmployeeData> storeEmployeeDataArrayList;
    DatabaseReference databaseReference;

    public EmployeeListAdapter(Context c, ArrayList<StoreEmployeeData> p) {
        context = c;
        storeEmployeeDataArrayList = p;
    }

    @NonNull
    @Override
    public EmployeeListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.employee_list_adapter, parent, false);

        return new EmployeeListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeListAdapter.MyViewHolder holder, int position) {
        StoreEmployeeData storeEmployeeData = storeEmployeeDataArrayList.get(position);

        String name = storeEmployeeData.getUsername();
        String phone = storeEmployeeData.getUserPhone();
        String email = storeEmployeeData.getUserEmail();
        String nid = storeEmployeeData.getUserNid();
        String address = storeEmployeeData.getUserAddress();

        holder.nameText.setText(name);
        holder.phoneText.setText(phone);
        holder.emailText.setText(email);

        if(!nid.equals("Update Now")){
            holder.nidText.setText("NID: "+nid);
        } else{
            holder.nidText.setText("NID: Did not updated");
        }

        if(!address.equals("Update Now")){
            holder.addressText.setText("Address: "+address);
        } else{
            holder.addressText.setText("Address: Did not updated");
        }

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
                            Toast.makeText(context, "Employee Removed Permanently", Toast.LENGTH_SHORT).show();

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
        return storeEmployeeDataArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, phoneText, emailText, nidText, addressText;
        ImageView deleteEmployee;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            nameText = itemView.findViewById(R.id.employeeNameListId);
            phoneText = itemView.findViewById(R.id.employeePhoneListId);
            emailText = itemView.findViewById(R.id.employeeEmailListId);
            nidText = itemView.findViewById(R.id.employeeNidListId);
            addressText = itemView.findViewById(R.id.employeeAddressListId);

            deleteEmployee = itemView.findViewById(R.id.deleteEmployeeFromListId);
            databaseReference = FirebaseDatabase.getInstance().getReference("Employee Info");
        }
    }
}
