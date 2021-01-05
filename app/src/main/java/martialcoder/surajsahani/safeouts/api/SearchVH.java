package martialcoder.surajsahani.safeouts.api;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import martialcoder.surajsahani.safeouts.ItemClickListener;
import martialcoder.surajsahani.safeouts.R;


public class SearchVH extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView textSearchName,textSearchDist,txtSearchPeople;
    public ImageView ProductImage;
    public ItemClickListener listener;
    public SearchVH(@NonNull View itemView) {
        super(itemView);
        ProductImage=(ImageView)itemView.findViewById(R.id.search_card_img);
        textSearchName=(TextView) itemView.findViewById(R.id.search_card_title);
        textSearchDist=(TextView)itemView.findViewById(R.id.search_card_dist);
        txtSearchPeople=(TextView)itemView.findViewById(R.id.search_card_people);
    }

    public void setItemClickListener(ItemClickListener listener)
    {
        this.listener=listener;
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view , getAdapterPosition(),false);
    }
}
