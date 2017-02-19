package com.sebhero.overwatch;

/**
 * Created by trevl on 2017-02-17.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static ArrayAdapter<String> eventAdapter;
    public static EditText txtbIP;

    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public static void setEventAdapter(ArrayAdapter<String> eventAdapter) {
        PlaceholderFragment.eventAdapter = eventAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = null;
        if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);
//                TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//                textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            //txtb ip
            txtbIP = (EditText) rootView.findViewById(R.id.edTxtIP);

        } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
            rootView = inflater.inflate(R.layout.fragment_subpage1, container, false);
            ListView listV = (ListView) rootView.findViewById(R.id.listV);
            listV.setAdapter(eventAdapter);


        }
        return rootView;
    }

}