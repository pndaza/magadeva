package mm.pndaza.maghadeva.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import mm.pndaza.maghadeva.R;
import mm.pndaza.maghadeva.adapter.BookmarkAdapter;
import mm.pndaza.maghadeva.callback.SwipeToDeleteCallback;
import mm.pndaza.maghadeva.db.DBOpenHelper;
import mm.pndaza.maghadeva.model.Bookmark;
import mm.pndaza.maghadeva.utils.MDetect;
import mm.pndaza.maghadeva.utils.Rabbit;

public class BookmarkFragment extends Fragment {

    public interface OnBookmarkItemClickListener {
        void onBookmarkItemClick(int pageNumber);
    }

    private Context context;
    private RecyclerView bookmarkListView;
    TextView emptyInfoView;
    private ArrayList<Bookmark> bookmarks;
    private OnBookmarkItemClickListener callbackListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(MDetect.getDeviceEncodedText(getString(R.string.bookmark_mm)));
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_bookmark, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = view.getContext();
        //bind view
        bookmarkListView = view.findViewById(R.id.listView_bookmark);
        bookmarkListView.setLayoutManager(new LinearLayoutManager(context));
        bookmarkListView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        setupBookmarkList();

        emptyInfoView = view.findViewById(R.id.empty_info);
        setupEmptyInfoView(emptyInfoView);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            callbackListener = (OnBookmarkItemClickListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implemented OnWordListSelectedListener");

        }
    }
    private void setupBookmarkList() {
        bookmarks = DBOpenHelper.getInstance(context).getBookmarks();
        final BookmarkAdapter adapter = new BookmarkAdapter(bookmarks);
        bookmarkListView.setAdapter(adapter);

        adapter.setOnClickListener( view -> {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            int verseNumber = bookmarks.get(position).getVerseNumber();

            Log.d("pageNumber" , ""+verseNumber);

            callbackListener.onBookmarkItemClick(verseNumber);
        });

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(context, adapter));
        itemTouchHelper.attachToRecyclerView(bookmarkListView);

    }

    private void setupEmptyInfoView(TextView emptyInfoView){

        String info = getString(R.string.bookmark_empty);
        if (!MDetect.isUnicode()) {
            info = Rabbit.uni2zg(info);
        }
        emptyInfoView.setText(info);
        emptyInfoView.setVisibility(bookmarks.isEmpty() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_bookmark, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_clearAll) {
            clearBookmarks();
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearBookmarks() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context,R.style.AlertDialogTheme);

        String message = "သိမ်းမှတ်ထားသည်များကို ဖျက်မှာလား";
        String comfirm = "ဖျက်မယ်";
        String cancel = "မလုပ်တော့ဘူး";
        if (!MDetect.isUnicode()) {
            message = Rabbit.uni2zg(message);
            comfirm = Rabbit.uni2zg(comfirm);
            cancel = Rabbit.uni2zg(cancel);
        }

        alertDialog.setMessage(message)
                .setCancelable(true)
                .setPositiveButton(comfirm,
                        (dialog, id) -> {
                            DBOpenHelper.getInstance(context).removeAllBookmarks();
                            setupBookmarkList();
                            setupEmptyInfoView(emptyInfoView);
                        })
                .setNegativeButton(cancel, (dialog, id) -> {
                });
        alertDialog.show();
    }

}
