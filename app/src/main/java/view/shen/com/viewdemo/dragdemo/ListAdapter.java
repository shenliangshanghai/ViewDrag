package view.shen.com.viewdemo.dragdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author shenliang
 * @date 2019/6/28
 * @desc
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {
    private Context mContext;

    public ListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, viewGroup, false);
        return new ListViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder listViewHolder, int i) {
          TextView textView=listViewHolder.getViewById(android.R.id.text1);
          textView.setText("内容----"+i);
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    public static class ListViewHolder extends  RecyclerView.ViewHolder{
        private final View itemView;
        SparseArray<View> sparseArray=new SparseArray<>();

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        public <T extends View> T getViewById(int viewId){
            View view = sparseArray.get(viewId);
            if(view==null){
               view= itemView.findViewById(viewId);
               sparseArray.put(viewId,view);
            }
            return (T)view;
        }
    }
}
