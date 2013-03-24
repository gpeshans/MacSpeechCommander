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

		Currency entry = listValuta.get(position);
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			convertView = inflater.inflate(R.layout.rowlayout_currencies,
					parent, false);
		}

		ImageView image = (ImageView) convertView.findViewById(R.id.flag);
		image.setImageResource(entry.getFlag());

		TextView shortName = (TextView) convertView
				.findViewById(R.id.short_name);
		shortName.setText(entry.getShortName());

		TextView fullName = (TextView) convertView.findViewById(R.id.full_name);
		fullName.setText(entry.getFullNameMac());

		TextView tvMail = (TextView) convertView.findViewById(R.id.value);
		tvMail.setText(entry.getAverage());

		return convertView;
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