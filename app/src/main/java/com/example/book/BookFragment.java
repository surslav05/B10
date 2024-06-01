package com.example.book;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;


import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

public class BookFragment extends Fragment {
    public EditText mTitleField;
    public Book mBook;
    public Button mDateButton;
    public CheckBox mReadedCheckBox;
    private Button mReportButton;
private File mPhotoFile;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private static final String ARG_BOOK_ID = "book_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID bookId = (UUID) getArguments().getSerializable(ARG_BOOK_ID);
        mBook = BookLab.get(getActivity()).getBook(bookId);
        mPhotoFile = BookLab.get(getActivity()).getPhotoFile(mBook);
    }
    @Override
    public void onPause() {
        super.onPause();
        BookLab.get(getActivity()).updateBook(mBook);
    }
    public static BookFragment newInstance(UUID bookId)
    {
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK_ID, bookId);
        BookFragment fragment = new BookFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_book, container,
                false);
        mTitleField = (EditText) v.findViewById(R.id.book_title);
        mTitleField.setText(mBook.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBook.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mDateButton = (Button) v.findViewById(R.id.book_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mBook.getDate());
                dialog.setTargetFragment(BookFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });
        mReadedCheckBox = (CheckBox) v.findViewById(R.id.book_readed);
        mReadedCheckBox.setChecked(mBook.isReaded());
        mReadedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBook.setReaded(isChecked);
            }

        });
        mReportButton = (Button) v.findViewById(R.id.book_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getBookReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.book_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });
        mPhotoButton = (ImageButton) v.findViewById(R.id.book_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        PackageManager packageManager = getActivity().getPackageManager();
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        if (canTakePhoto) {
            Uri uri;

            if (Build.VERSION.SDK_INT < 24)
                uri = Uri.fromFile(mPhotoFile);
            else
                uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            mPhotoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(captureImage, REQUEST_PHOTO);
                }
            });
            mPhotoView = (ImageView) v.findViewById(R.id.book_photo);
            updatePhotoView();
            return v;
        }
            return v;
        }



    private void updateDate() {
        mDateButton.setText(mBook.getDate().toString());
    }

    private String getBookReport() {
        String readedString = null;
        if (mBook.isReaded()){
            readedString = getString(R.string.book_report_readed);
        }else {
            readedString = getString(R.string.book_report_unreaded);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.getDateInstance(DateFormat.MEDIUM).format(mBook.getDate());
        String report = getString(R.string.book_report, mBook.getTitle(), dateString, readedString);
        return report;
    }
    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
        Bitmap bitmap = PictureUtils.getScaledBitmap( mPhotoFile.getPath(), getActivity());
        mPhotoView.setImageBitmap(bitmap); }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        if (requestCode == REQUEST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mBook.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_PHOTO){
            updatePhotoView();
        }
    }
}
