package com.example.tuionf.listenevernote.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.evernote.client.android.type.NoteRef;
import com.evernote.edam.type.LinkedNotebook;
import com.evernote.edam.type.Notebook;
import com.example.tuionf.listenevernote.R;
import com.example.tuionf.listenevernote.Task.FindNotebooksTask;

import net.vrallev.android.task.TaskResult;

import java.util.ArrayList;
import java.util.List;

import static com.iflytek.cloud.VerifierResult.TAG;

public class NotebookFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int MAX_NOTES = 20;
    private String mQuery;
    private Notebook mNotebook;
    private LinkedNotebook mLinkedNotebook;
    private List<NoteRef> mNoteRefList = new ArrayList<>();
    private NotebookAdapter notebookAdapter;
    private List<Notebook> mNotebooks;
    private ListView menuListView;
    private Activity activity;


    public NotebookFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NoteFragment.
     */
    public static NotebookFragment newInstance(String param1, String param2) {
        NotebookFragment fragment = new NotebookFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        View view = inflater.inflate(R.layout.fragment_notebook, container, false);
        menuListView = view.findViewById(R.id.notebook_listView);

//        RefreshLayout refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
//        refreshLayout.setRefreshHeader(new MaterialHeader(this).setShowBezierWave(true));
//
//        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh(RefreshLayout refreshlayout) {
////                loadNoteData();
////                loadNotebookData();
//                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
//            }
//        });
//
//        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
//            @Override
//            public void onLoadmore(RefreshLayout refreshlayout) {
//                refreshlayout.finishLoadmore(2000/*,false*/);//传入false表示加载失败
//            }
//        });

        loadNotebookData();
        return view;
    }

    private void loadNotebookData() {
        new FindNotebooksTask().start(this, "personal");
    }

    @TaskResult(id = "personal")
    public void onFindNotebooks(List<Notebook> notebooks) {
//        mSwipeRefreshLayout.setRefreshing(false);
        Log.e(TAG, "onFindNotebooks: "+notebooks.size()+notebooks.get(0).getName() );
        mNotebooks = notebooks;

        notebookAdapter = new NotebookAdapter();
        menuListView.setAdapter(notebookAdapter);

    }

    private class NotebookAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mNotebooks.size();
        }

        @Override
        public Notebook getItem(int position) {
            return mNotebooks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NotebookViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(activity).inflate(android.R.layout.simple_list_item_1, parent, false);
                viewHolder = new NotebookViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (NotebookViewHolder) convertView.getTag();
            }

            Log.e(TAG, "getView:------ ");

            Notebook notebook = getItem(position);
            Log.e(TAG, "getView:------ "+position +notebook.getName() );
            viewHolder.mTextView1.setText(notebook.getName());

            return convertView;
        }
    }

    private static class NotebookViewHolder {

        private final TextView mTextView1;

        public NotebookViewHolder(View view) {
            mTextView1 = (TextView) view.findViewById(android.R.id.text1);
        }
    }
}
