package com.example.sadmanbn.ui.notifications;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.sadmanbn.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private ListView listView;  // 列表控件
    private List<Entity> entityList;  // 实体列表
    private BaseAdapter baseAdapter; // 适配器
    private TextView textView;   // 标题

    // 构造方法
    public NotificationsFragment() {
        // 初始化实体列表
        entityList = new ArrayList<>();

        // 实例化适配器
        baseAdapter = new BaseAdapter() {

            // 返回列表项的数量
            @Override
            public int getCount() {
                return entityList.size();
            }

            // 返回指定位置的列表项
            @Override
            public Object getItem(int position) {
                return entityList.get(position);
            }

            // 返回指定位置列表项的ID
            @Override
            public long getItemId(int position) {
                return position;
            }

            // 返回指定位置的视图
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = View.inflate(getContext(), R.layout.notification_item, null);
                    TextView courseNameTextView = convertView.findViewById(R.id.courseName);
                    TextView examDateTextView = convertView.findViewById(R.id.examDate);

                    // 创建ViewHolder实例并设置Tag
                    ViewHolder viewHolder = new ViewHolder(courseNameTextView, examDateTextView);
                    convertView.setTag(viewHolder);
                }

                // 从Tag中获取ViewHolder
                ViewHolder viewHolder = (ViewHolder) convertView.getTag();
                Entity entity = (Entity) getItem(position);

                // 设置课程名称和考试日期
                viewHolder.getCourseNameTextView().setText(entity.getCourseName());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                viewHolder.getExamDateTextView().setText(sdf.format(entity.getExamDate()));

                return convertView;
            }
        };
    }

    // 获取NotificationsFragment布局对应的视图
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 获取NotificationsFragment布局对应的视图
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        // 找列表视图、设置适配器、设置列表项单击监听
        listView = root.findViewById(R.id.listView);
        textView = root.findViewById(R.id.textView);

        listView.setAdapter(baseAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // 单击列表项查看详情
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Entity entity = (Entity) listView.getItemAtPosition(position);
                new AlertDialog.Builder(getContext())
                        .setTitle(entity.getCourseName())
                        .setMessage(entity.getExamDetails())
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            // 单击标题添加新列表项
            @Override
            public void onClick(View v) {
                entityList.add(new Entity("Chemical", new Date(), "时间 9:00-11:00, 实验楼222"));
                baseAdapter.notifyDataSetChanged();  // 通知适配器数据已更新，刷新列表
            }
        });

        // 实例化视图模型字段
        notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel.class);

        // 获取可观察的数据容器
        LiveData<List<Entity>> mutableLiveEntityList = notificationsViewModel.getMutableLiveEntityList();

        // 观察数据变化
        mutableLiveEntityList.observe(this, new Observer<List<Entity>>() {
            @Override
            public void onChanged(List<Entity> entityList) {
                NotificationsFragment.this.entityList.addAll(entityList);
            }
        });

        return root;
    }

    // 列表项对应的视图
    class ViewHolder {
        private TextView courseNameTextView;
        private TextView examDateTextView;

        // 构造方法
        public ViewHolder(TextView tv_courseName, TextView tv_examDetails) {
            this.courseNameTextView = tv_courseName;
            this.examDateTextView = tv_examDetails;
        }

        // 获取课程名称TextView
        public TextView getCourseNameTextView() {
            return courseNameTextView;
        }

        // 获取考试日期TextView
        public TextView getExamDateTextView() {
            return examDateTextView;
        }
    }
}
