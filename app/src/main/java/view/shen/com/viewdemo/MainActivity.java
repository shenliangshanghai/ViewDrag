package view.shen.com.viewdemo;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import view.shen.com.viewdemo.dragdemo.SlideUpPanelActivity;
import view.shen.com.viewdemo.dragdemo.SlideUpPanelListActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        RecyclerView rvList = findViewById(R.id.rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        List<String> stringList = Arrays.asList("SlideUpPanelLayoutDemo","SlideUpPanelListDemo");
        List<Class> clazzList=new ArrayList<>();

        clazzList.add(SlideUpPanelActivity.class);
        clazzList.add(SlideUpPanelListActivity.class);
        MyAdapter myAdapter = new MyAdapter(this, stringList);
        rvList.setAdapter(myAdapter);
        myAdapter.setOnClickObserve(new MyAdapter.OnClickObserve() {
            @Override
            public void onClick(int position) {
                intentTo(clazzList.get(position));
            }
        });
    }

    private void intentTo(Class clazz) {
        startActivity(new Intent(this, clazz));
    }

    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ListViewHolder> {
        private Context mContext;
        private List<String> list;
        private OnClickObserve onClickObserve;

        public void setOnClickObserve(OnClickObserve onClickObserve) {
            this.onClickObserve = onClickObserve;
        }

        public MyAdapter(Context mContext, List<String> stringList) {
            this.mContext = mContext;
            this.list = stringList;
        }

        @NonNull
        @Override
        public MyAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View inflate = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, viewGroup, false);
            return new MyAdapter.ListViewHolder(inflate);
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.ListViewHolder listViewHolder, int i) {
            TextView textView = listViewHolder.getViewById(android.R.id.text1);
            textView.setText(list.get(i));
            listViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickObserve != null) {
                        onClickObserve.onClick(i);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public static class ListViewHolder extends RecyclerView.ViewHolder {
            private final View itemView;
            SparseArray<View> sparseArray = new SparseArray<>();

            public ListViewHolder(@NonNull View itemView) {
                super(itemView);
                this.itemView = itemView;
            }

            public <T extends View> T getViewById(int viewId) {
                View view = sparseArray.get(viewId);
                if (view == null) {
                    view = itemView.findViewById(viewId);
                    sparseArray.put(viewId, view);
                }
                return (T) view;
            }
        }

        public interface OnClickObserve {
            void onClick(int position);
        }
    }

}
