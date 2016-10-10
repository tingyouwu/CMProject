package com.kw.app.medicine.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kw.app.commonlib.utils.ImageLoaderUtil;
import com.kw.app.commonlib.utils.TimeUtil;
import com.kw.app.imlib.bean.RongConversation;
import com.kw.app.medicine.R;
import com.kw.app.medicine.bean.conversation.PrivateConversation;
import com.kw.app.widget.BaseRecyclerViewHolder;
import com.kw.app.widget.adapter.BaseRecyclerViewAdapter;
import com.kw.app.widget.view.UnreadTextView;

import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * @Decription 联系人 适配器
 */
public class ConversationAdapter extends BaseRecyclerViewAdapter<RongConversation> {
    public ConversationAdapter(Context context, List data) {
        super(context, R.layout.item_conversation, data);
    }

    @Override
    protected void convert(BaseRecyclerViewHolder helper, final RongConversation item, int position) {
        ImageView icon = helper.getView(R.id.iv_recent_avatar);
        TextView time = helper.getView(R.id.tv_recent_time);
        TextView name = helper.getView(R.id.tv_recent_name);
        TextView msg = helper.getView(R.id.tv_recent_msg);
        final UnreadTextView unreadtv = helper.getView(R.id.tv_recent_unread);
        View line = helper.getView(R.id.line);

        msg.setText(item.getLastMessageContent());
        name.setText(item.getcName());
        time.setText(TimeUtil.getChatTime(false,item.getLastMessageTime()));
        if(position == mData.size()-1){
            //最后一个不用显示线条
            line.setVisibility(View.GONE);
        }

        //会话图标
        Object obj = item.getAvatar();
        if(obj instanceof String){
            String avatar=(String)obj;
            ImageLoaderUtil.loadCircle(mContext, avatar, R.mipmap.img_contact_default, icon);
        }else{
            int defaultRes = (int)obj;
            ImageLoaderUtil.loadCircle(mContext, defaultRes, icon);
        }

        if(item instanceof PrivateConversation){
            RongIMClient.getInstance().getUnreadCount(Conversation.ConversationType.PRIVATE, item.getcId(),
                    new RongIMClient.ResultCallback<Integer>() {
                        @Override
                        public void onSuccess(Integer integer) {
                            int unreadCount = integer;
                            //开发者根据自己需求自行处理接下来的逻辑
                            unreadtv.setUnread(unreadCount);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
        }else{
            //查询指定未读消息数
            int unread = item.getUnReadCount();
            unreadtv.setUnread(unread);
        }

        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.onClick(v.getContext());
            }
        });

    }

}
