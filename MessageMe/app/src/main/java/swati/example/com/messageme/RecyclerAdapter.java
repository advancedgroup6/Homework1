package swati.example.com.messageme;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by swati
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private ArrayList<Message> msgList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name,date,msg;
        public ImageView isRead;
        public ImageButton isLocked;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.textViewSender);
            date = (TextView) view.findViewById(R.id.textViewDate);
            msg = (TextView) view.findViewById(R.id.textViewMsg);
            isRead = (ImageView) view.findViewById(R.id.imageViewRead);
            isLocked = (ImageButton) view.findViewById(R.id.imageButtonLock);
        }

    }

    public RecyclerAdapter(ArrayList<Message> msgList) {
        this.msgList = msgList;
    }

    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item, parent, false);

        return new RecyclerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerAdapter.MyViewHolder holder, final int position) {
        final Message message = msgList.get(position);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date newDate = null;
        try {
            newDate = format.parse(message.getDate());
            format = new SimpleDateFormat("dd/MM/yy, hh:mm a");
            String date = format.format(newDate);
            holder.date.setText(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.name.setText(message.getSender());
        String msgSmall = message.getMsg();
        if(msgSmall!=null && msgSmall.length()>25){
            msgSmall = msgSmall.substring(0,24);
        }
        holder.msg.setText(msgSmall);
        if(message.isRead()){
            holder.isRead.setImageResource(R.drawable.circle_grey);
        } else {
            holder.isRead.setImageResource(R.drawable.circle_blue);
        }
        if(message.isLocked()){
            holder.isLocked.setBackgroundResource(R.drawable.lock);
        } else {
            holder.isLocked.setBackgroundResource(R.drawable.lock_open);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(holder.itemView.getContext(),ReadMsgActivity.class);
                    intent.putExtra("message",  message);
                    holder.itemView.getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }
}
