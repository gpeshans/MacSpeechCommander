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
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			convertView = inflater.inflate(R.layout.rowlayout_prices, viewGroup,
					false);
		}

		/*ImageView image = (ImageView) convertView.findViewById(R.id.image);
		image.setImageDrawable(context.getResources().getDrawable(entry.getImage()));*/
		
		TextView name = (TextView) convertView.findViewById(R.id.value);
		name.setText(entry.getValue().toString());

		TextView value = (TextView) convertView.findViewById(R.id.name);
		value.setText(entry.getName());

		return convertView;
	}

}
