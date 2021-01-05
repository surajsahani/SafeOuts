package martialcoder.surajsahani.safeouts;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView textproductName,textProductInfo,txtProductPrice;
    public ImageView ProductImage,ProductImagelogo;
    public ItemClickListener listener;
    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        ProductImage=(ImageView)itemView.findViewById(R.id.cardimg);
        ProductImagelogo=(ImageView)itemView.findViewById(R.id.card_logo_img);
        textproductName=(TextView) itemView.findViewById(R.id.card_title);
        textProductInfo=(TextView)itemView.findViewById(R.id.card_dist);
        txtProductPrice=(TextView)itemView.findViewById(R.id.card_people);
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
