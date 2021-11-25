package com.example.nearbyhospitals.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nearbyhospitals.R;
import com.example.nearbyhospitals.model.Hospital;

import java.util.List;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder> {
    private List<Hospital> hospitalList;
    private iClickListener mIClickListener;



    public HospitalAdapter(List<Hospital> hospitalList, iClickListener listener) {
        this.hospitalList = hospitalList;
        this.mIClickListener = listener;
    }

    public interface iClickListener{
        void onClickCheckLocation(Hospital hos);
    }

    @NonNull
    @Override
    public HospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hospiyal,parent,false);
        return new HospitalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HospitalViewHolder holder, int position) {
        Hospital hos = hospitalList.get(position);
        if (hos == null){
            return;
        }
        holder.tvnameHos.setText(hos.getNameHospital());
        holder.tvhosLong.setText(hos.getHospitalLong());
        holder.tvhosLat.setText(hos.getHospitalLat());
        holder.btn_checkLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIClickListener.onClickCheckLocation(hos);
            }
        });


    }

    @Override
    public int getItemCount() {
        if (hospitalList!= null){
            return hospitalList.size();
        }
        return 0;
    }

    class HospitalViewHolder extends RecyclerView.ViewHolder{
        private TextView tvnameHos,tvhosLong,tvhosLat;
        private Button btn_checkLocation;
        public HospitalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvnameHos = itemView.findViewById(R.id.tv_nameHospital);
            tvhosLong = itemView.findViewById(R.id.tv_hospitalLong);
            tvhosLat = itemView.findViewById(R.id.tv_hospitalLat);
            btn_checkLocation = itemView.findViewById(R.id.btn_checkLocation);


        }
    }
}
