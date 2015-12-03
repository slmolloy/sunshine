package com.example.android.sunshine.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

public class DetailActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.action_view_location:
                openPreferredLocationMap();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationMap() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String zip = sharedPref.getString(
                getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));

        Uri location = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", zip)
                .build();

        Intent map = new Intent(Intent.ACTION_VIEW);
        map.setData(location);

        if (map.resolveActivity(getPackageManager()) != null) {
            startActivity(map);
        } else {
            Log.d(LOG_TAG, "Couldn't call " + zip + ", no receiving apps installed!");
        }
    }

    /**
     * Detail fragment containing simple view.
     */
    public static class DetailFragment extends Fragment {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        private Context mContext = null;

        private ShareActionProvider mShareActionProvider;
        private static final String FORECAST_SHARE_HASHTAG = "#SunshinApp";
        private String mWeatherMessage;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            mContext = context;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                mWeatherMessage = intent.getStringExtra(Intent.EXTRA_TEXT);
                ((TextView) rootView.findViewById(R.id.detail_text))
                        .setText(mWeatherMessage);
            }

            String html = "<p>A long time ago in a <img src=\"android.svg\">ool <b>galaxy</b> far far away lived a young jedi. The story shall will always <img>ontinue on no matter what happens. After all there are a total of 9 movies to be made.</p>";

            TextView tv1 = (TextView) rootView.findViewById(R.id.image_demo_text1);
            int lineHeight = tv1.getLineHeight();
            tv1.setText(Html.fromHtml(html, new HtmlImageHelper(lineHeight, lineHeight), null));

            TextView tv2 = (TextView) rootView.findViewById(R.id.image_demo_text2);
            lineHeight = tv2.getLineHeight();
            tv2.setText(Html.fromHtml(html, new HtmlImageHelper(lineHeight, lineHeight), null));

            TextView tv3 = (TextView) rootView.findViewById(R.id.image_demo_text3);
            lineHeight = tv3.getLineHeight();
            tv3.setText(Html.fromHtml(html, new HtmlImageHelper(lineHeight, lineHeight), null));

            return rootView;
        }

        public Drawable getSvgImageFromResource(Context context, String source, int width, int height) {
            Drawable result = null;
            try {
                SVG svg = SVG.getFromResource(context.getResources(), R.raw.ccd);
                svg.setDocumentWidth(width);
                svg.setDocumentHeight(height);
                result = new PictureDrawable(svg.renderToPicture());
                result.setBounds(0, 0, result.getIntrinsicWidth(), result.getIntrinsicHeight());
            } catch (SVGParseException e) {
                Log.e(LOG_TAG, "Error loading [" + source + "] with exception: " + e.toString());
            }
            return result;
        }

        public class HtmlImageHelper implements ImageGetter {
            private int width = -1;
            private int height = -1;

            public HtmlImageHelper(int width, int height) {
                if (width > 0) { this.width = width; }
                if (height > 0) { this.height = height; }
            }

            @Override
            public Drawable getDrawable(String source) {
                return getSvgImageFromResource(mContext, source, width, height);
            }
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.detailfragment, menu);

            MenuItem menuItem = menu.findItem(R.id.action_share);
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

            setShareIntent(createShareIntent());
        }

        private void setShareIntent(Intent shareIntent) {
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(shareIntent);
            } else {
                Log.d(LOG_TAG, "Share Action Provider is null?");
            }
        }

        private Intent createShareIntent() {
            return new Intent(Intent.ACTION_SEND)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, mWeatherMessage + " " + FORECAST_SHARE_HASHTAG);
        }
    }
}
