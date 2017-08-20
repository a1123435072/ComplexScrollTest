package com.itheima.complexscrolltest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(new RecyclerView.Adapter() {
			@Override
			public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
				View itemView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, null, false);
				MyViewHolder viewHolder = new MyViewHolder(itemView);
				return viewHolder;
			}

			@Override
			public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
				MyViewHolder mVH = (MyViewHolder) holder;
				mVH.tv.setText("条目" + position);
			}

			@Override
			public int getItemCount() {
				return 30;
			}

			class MyViewHolder extends RecyclerView.ViewHolder {

				private TextView tv;

				public MyViewHolder(View itemView) {
					super(itemView);
					tv = (TextView) itemView.findViewById(android.R.id.text1);
				}
			}
		});
	}
}
