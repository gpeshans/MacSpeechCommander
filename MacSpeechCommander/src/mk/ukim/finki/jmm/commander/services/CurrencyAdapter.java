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

public class CurrencyAdapter extends ArrayAdapter<Currency> {
	private Context context;

	private List<Currency> listValuta;

	static class ViewHolder {
		public TextView tvShortName;
		public TextView tvFullName;
		public TextView tvValue;
		public ImageView image;
	}

	public CurrencyAdapter(Context context, int resourceId,
			List<Currency> listValuta) {
		super(context, resourceId, listValuta);
		this.context = context;
		this.listValuta = listValuta;
	}

	public int getCount() {
		return listValuta.size();
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View rowView = convertView;
		Currency entry = listValuta.get(position);
		if (rowView == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			rowView = inflater.inflate(R.layout.rowlayout_currencies,
					parent, false);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.tvShortName = (TextView) rowView.findViewById(R.id.short_name);
			viewHolder.tvFullName = (TextView) rowView.findViewById(R.id.full_name);
			viewHolder.tvValue = (TextView) rowView.findViewById(R.id.value);
			viewHolder.image = (ImageView) rowView
					.findViewById(R.id.flag);
			rowView.setTag(viewHolder);

			
		}
		
		

		/*ImageView image = (ImageView) convertView.findViewById(R.id.flag);
		image.setImageResource(entry.getFlag());
		TextView shortName = (TextView) convertView
				.findViewById(R.id.short_name);
		shortName.setText(entry.getShortName());

		TextView fullName = (TextView) convertView.findViewById(R.id.full_name);
		fullName.setText(entry.getFullNameMac());

		TextView value = (TextView) convertView.findViewById(R.id.value);
		value.setText(entry.getAverage());*/
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
		
		holder.image.setImageResource(entry.getFlag());
		holder.tvShortName.setText(entry.getShortName());
		holder.tvFullName.setText(entry.getFullNameMac());
		holder.tvValue.setText(entry.getAverage());

		

		return rowView;
	}

	/*
	 * public View getView(int position, View convertView, ViewGroup viewGroup)
	 * { Currency entry = listValuta.get(position); if (convertView == null) {
	 * LayoutInflater inflater = ((Activity) context).getLayoutInflater();
	 * convertView = inflater .inflate(R.layout.rowlayout_currencies, viewGroup,
	 * false); }
	 * 
	 * ImageView image = (ImageView) convertView.findViewById(R.id.flag);
	 * image.setImageResource(entry.getFlag());
	 * 
	 * TextView shortName = (TextView) convertView
	 * .findViewById(R.id.short_name); shortName.setText(entry.getShortName());
	 * 
	 * TextView fullName = (TextView) convertView.findViewById(R.id.full_name);
	 * fullName.setText(entry.getFullNameMac());
	 * 
	 * TextView tvMail = (TextView) convertView.findViewById(R.id.value);
	 * tvMail.setText(entry.getAverage());
	 * 
	 * //notifyDataSetChanged();
	 * 
	 * return convertView; }
	 */

}