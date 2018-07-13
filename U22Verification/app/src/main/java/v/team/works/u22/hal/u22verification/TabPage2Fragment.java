package v.team.works.u22.hal.u22verification;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * TabLayoutのFragmentクラス2.
 * Fragmentはレイアウトxmlとセットになっている。
 * Fragmentを設定したTab内で行われる処理は、このクラス内に記述する。
 *
 * @author Taiga Hirai
 */
public class TabPage2Fragment extends Fragment {

    private static final String ARG_PARAM = "page";
    private String mParam;
    private TabPage1Fragment.OnFragmentInteractionListener mListener;

    /**
     * コンストラクタ.
     */
    public TabPage2Fragment() {
    }

    public static TabPage2Fragment newInstance(int page) {
        TabPage2Fragment fragment = new TabPage2Fragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam = getArguments().getString(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        int page = getArguments().getInt(ARG_PARAM, 0);
        View view = inflater.inflate(R.layout.fragment_tab_page2, container, false);
        TextView tvTextView = view.findViewById(R.id.tvTab2Text);
        tvTextView.setText("Fragment2-Page" + page);

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TabPage1Fragment.OnFragmentInteractionListener) {
            mListener = (TabPage1Fragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
