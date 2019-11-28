package com.jesper.shutapp.Sqlite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jesper.shutapp.R;

import java.util.Collections;
import java.util.List;

public class ReportListAdapter extends RecyclerView.Adapter<ReportListAdapter.ReportViewHolder> {
    class ReportViewHolder extends RecyclerView.ViewHolder {
        private final TextView reportItemView;

        private ReportViewHolder(View itemView) {
            super(itemView);
            reportItemView = itemView.findViewById(R.id.textView);
        }
    }


    private final LayoutInflater mInflater;
    private List<Report> mReports = Collections.emptyList();

    ReportListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.reportrecyclerview_item, parent, false);
        return new ReportViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder( ReportViewHolder holder, int position) {
        Report current = mReports.get(position);
        holder.reportItemView.setText(current.getReportTxt());
    }

    void setReports(List<Report> reports) {
        mReports = reports;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mReports != null)
            return mReports.size();
        else
            return 0;
    }
}

