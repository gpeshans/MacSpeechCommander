package mk.ukim.finki.jmm.commander.services;

import java.util.List;

import mk.ukim.finki.jmm.commander.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PricesAdapter extends ArrayAdapter<Product> {
	private Context context;

	private List<Product> listProduct;

	static class ViewHolder {
		public TextView tvName;
		public TextView tvValue;
		public ImageView image;
	}

	public PricesAdapter(Context context, int resourceId,
			List<Product> listProduct) {
		super(context, resourceId, listProduct);
		this.context = context;
		this.listProduct = listProduct;
	}

	public int getCount() {
		return listProduct.size();
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup viewGroup) {
		Product entry = listProduct.get(position);
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			rowView = inflater.inflate(R.layout.rowlayout_prices, viewGroup,
					false);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.tvName = (TextView) rowView.findViewById(R.id.name);
			viewHolder.tvValue = (TextView) rowView.findViewById(R.id.value);
			viewHolder.image = (ImageView) rowView.findViewById(R.id.image);
			rowView.setTag(viewHolder);
		}		

		ViewHolder holder = (ViewHolder) rowView.getTag();
		holder.image.setImageResource(entry.getImage());
		holder.tvName.setText(entry.getName());
		holder.tvValue.setText(entry.getValue());

		return rowView;
	}

}
