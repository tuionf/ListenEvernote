package com.example.tuionf.listenevernote.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.evernote.client.android.type.NoteRef;
import com.evernote.edam.type.LinkedNotebook;
import com.evernote.edam.type.Notebook;
import com.example.tuionf.listenevernote.R;
import com.example.tuionf.listenevernote.Task.FindNotesTask;
import com.example.tuionf.listenevernote.Task.GetNoteHtmlTask;

import net.vrallev.android.task.TaskResult;

import java.util.ArrayList;
import java.util.List;

public class NoteFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int MAX_NOTES = 20;
    private String mQuery;
    private Notebook mNotebook;
    private LinkedNotebook mLinkedNotebook;
    private List<NoteRef> mNoteRefList = new ArrayList<>();
    private NoteAdapter mAdapter;
    private ListView mListView;
    private Activity activity;


    public NoteFragment() {
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
    public static NoteFragment newInstance(String param1, String param2) {
        NoteFragment fragment = new NoteFragment();
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
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        mListView = view.findViewById(R.id.note_listView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new GetNoteHtmlTask(mNoteRefList.get(position))
                        .start(activity, "html");
            }
        });

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

        loadNoteData();
        return view;
    }

    private void loadNoteData() {
        new FindNotesTask(0, MAX_NOTES, mNotebook, mLinkedNotebook, mQuery).start(this);
        mQuery = null;
    }

    @TaskResult
    public void onFindNotes(List<NoteRef> noteRefList) {
        mNoteRefList = noteRefList;
        mAdapter = new NoteAdapter();
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private class NoteAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mNoteRefList.size();
        }

        @Override
        public NoteRef getItem(int position) {
            return mNoteRefList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NoteViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(activity).inflate(R.layout.yinxiang_note_item, parent, false);
                viewHolder = new NoteViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (NoteViewHolder) convertView.getTag();
            }

            NoteRef noteRef = getItem(position);
            viewHolder.note_title.setText(noteRef.getTitle());
//            viewHolder.note_intro.setText(noteRef.getGuid());

            return convertView;
        }
    }

    private static class NoteViewHolder {

        private final TextView note_title;
        private final TextView note_intro;

        public NoteViewHolder(View view) {
            note_title = (TextView) view.findViewById(R.id.note_title);
            note_intro = (TextView) view.findViewById(R.id.note_intro);
        }
    }

}
