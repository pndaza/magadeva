package mm.pndaza.maghadeva.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import mm.pndaza.maghadeva.R;
import mm.pndaza.maghadeva.db.DBOpenHelper;
import mm.pndaza.maghadeva.model.Bookmark;
import mm.pndaza.maghadeva.utils.MDetect;
import mm.pndaza.maghadeva.utils.MyanNumber;
import mm.pndaza.maghadeva.utils.Rabbit;


public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {

    private ArrayList<Bookmark> bookmarkList;
    private View.OnClickListener onClickListener;

    private Context context;

    public BookmarkAdapter(ArrayList<Bookmark> bookmarkList) {
        this.bookmarkList = bookmarkList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // reuse layout because layout are same
        View wordListItemView = inflater.inflate(R.layout.bookmarklist_row_item, parent, false);
        return new ViewHolder(wordListItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Bookmark bookmark = bookmarkList.get(position);
        holder.tvNote.setText(bookmark.getNote()); // note are saved as device encoding
        String verseNumber = MDetect.getDeviceEncodedText("လင်္ကာပိုဒ်ရေ - ") + MyanNumber.toMyanmar(bookmark.getVerseNumber());
        holder.tvVerseNumber.setText(verseNumber);
    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNote;
        TextView tvVerseNumber;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNote = itemView.findViewById(R.id.tv_note);
            tvVerseNumber = itemView.findViewById(R.id.tv_pageNumber);

            itemView.setTag(this);
            itemView.setOnClickListener(onClickListener);
        }
    }

    public void setOnClickListener(View.OnClickListener clickListener) {
        onClickListener = clickListener;
    }


    public void deleteItem(int position){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context,R.style.AlertDialogTheme);
        String message = "သိမ်းမှတ်ထားသည်ကို ဖျက်မှာလား";
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
                            DBOpenHelper.getInstance(context).removeFromBookmark(position);
                            bookmarkList.remove(position);
                            notifyDataSetChanged();
                        })
                .setNegativeButton(cancel, (dialog, id) -> {
                    notifyDataSetChanged();
                });
        alertDialog.show();

    }


}
