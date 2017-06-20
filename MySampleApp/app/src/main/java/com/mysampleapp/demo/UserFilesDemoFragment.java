package com.mysampleapp.demo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amazonaws.mobile.AWSConfiguration;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.content.ContentItem;
import com.amazonaws.mobile.content.ContentProgressListener;
import com.amazonaws.mobile.content.UserFileManager;
import com.amazonaws.mobile.util.ImageSelectorUtils;
import com.amazonaws.regions.Regions;
import com.mysampleapp.R;
import com.mysampleapp.SignInHandler;

import java.io.File;
import java.util.concurrent.CountDownLatch;

public class UserFilesDemoFragment extends DemoFragmentBase {
    public static final String S3_PREFIX_PUBLIC = "public/";
    public static final String S3_PREFIX_PRIVATE = "private/";
    public static final String S3_PREFIX_PROTECTED = "protected/";
    public static final String S3_PREFIX_UPLOADS = "uploads/";

    /** Permission Request Code (Must be < 256). */
    private static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 93;

    /** Upload Request Code for uploads folder action **/
    private static final int UPLOAD_REQUEST_CODE = 112;

    /** Log tag. */
    private static final String LOG_TAG = UserFilesDemoFragment.class.getSimpleName();

    private final CountDownLatch userFileManagerCreatingLatch = new CountDownLatch(1);

    /** The user file manager. Used on uploads folder */
    private UserFileManager userFileManager;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_demo_user_files, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Action for public folder button
        view.findViewById(R.id.button_userFiles_public)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        browse(AWSConfiguration.AMAZON_S3_USER_FILES_BUCKET, S3_PREFIX_PUBLIC, AWSConfiguration.AMAZON_S3_USER_FILES_BUCKET_REGION);
                    }
                });

        // Action for uploads folder button
        view.findViewById(R.id.button_userFiles_upload)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        upload(AWSConfiguration.AMAZON_S3_USER_FILES_BUCKET, AWSConfiguration.AMAZON_S3_USER_FILES_BUCKET_REGION);
                    }
                });

        // Action for private folder button
        view.findViewById(R.id.button_userFiles_private)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final boolean isUserSignedIn = AWSMobileClient.defaultMobileClient()
                                .getIdentityManager()
                                .isUserSignedIn();

                        if (isUserSignedIn) {
                            final String identityId = AWSMobileClient.defaultMobileClient()
                                    .getIdentityManager()
                                    .getCachedUserID();
                            browse(AWSConfiguration.AMAZON_S3_USER_FILES_BUCKET,
                                    S3_PREFIX_PRIVATE + identityId + "/", AWSConfiguration.AMAZON_S3_USER_FILES_BUCKET_REGION);
                            return;
                        } else {
                            promptSignin();
                        }
                    }
                });

        // Action for protected folder button
        view.findViewById(R.id.button_userFiles_protected)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final boolean isUserSignedIn = AWSMobileClient.defaultMobileClient()
                                .getIdentityManager()
                                .isUserSignedIn();

                        if (isUserSignedIn) {
                            final String identityId = AWSMobileClient.defaultMobileClient()
                                    .getIdentityManager()
                                    .getCachedUserID();
                            browse(AWSConfiguration.AMAZON_S3_USER_FILES_BUCKET,
                                    S3_PREFIX_PROTECTED, AWSConfiguration.AMAZON_S3_USER_FILES_BUCKET_REGION);
                            return;
                        } else {
                            browse(AWSConfiguration.AMAZON_S3_USER_FILES_BUCKET,
                                    S3_PREFIX_PROTECTED, AWSConfiguration.AMAZON_S3_USER_FILES_BUCKET_REGION);
                        }
                    }
                });

    }

    private void promptSignin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle(
                R.string.main_fragment_title_user_files)
                .setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                AWSMobileClient.defaultMobileClient()
                    .getIdentityManager()
                    .signInOrSignUp(getActivity(), new SignInHandler());
            }
        });
        builder.setMessage(R.string.user_files_demo_dialog_signin_message);
        builder.show();
    }

    private void browse(final String bucket, final String prefix, final Regions region) {
        UserFilesBrowserFragment fragment = new UserFilesBrowserFragment();
        Bundle args = new Bundle();
        args.putString(UserFilesBrowserFragment.BUNDLE_ARGS_S3_BUCKET, bucket);
        args.putString(UserFilesBrowserFragment.BUNDLE_ARGS_S3_PREFIX, prefix);
        args.putString(UserFilesBrowserFragment.BUNDLE_ARGS_S3_REGION, region.getName());
        fragment.setArguments(args);
        getActivity().getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .addToBackStack(null)
            .commit();
    }

    private void upload(final String bucket, final Regions region) {
        AWSMobileClient.defaultMobileClient()
                .createUserFileManager(getContext(), bucket, S3_PREFIX_UPLOADS, region,
                        new UserFileManager.BuilderResultHandler() {
                            @Override
                            public void onComplete(final UserFileManager userFileManager) {
                                if (!isAdded()) {
                                    userFileManager.destroy();
                                    return;
                                }

                                UserFilesDemoFragment.this.userFileManager = userFileManager;
                                userFileManagerCreatingLatch.countDown();
                            }
                        });

        final Activity activity = getActivity();

        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
            return;
        }

        // We have permission, so show the image selector.
        final Intent intent = ImageSelectorUtils.getImageSelectionIntent();
        startActivityForResult(intent, UPLOAD_REQUEST_CODE);
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if(requestCode == UPLOAD_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                final Uri uri = data.getData();
                Log.d(LOG_TAG, "data uri: " + uri);

                final String path = ImageSelectorUtils.getFilePathFromUri(getActivity(), uri);
                Log.d(LOG_TAG, "file path: " + path);
                final ProgressDialog dialog = new ProgressDialog(getActivity());
                dialog.setTitle(R.string.content_progress_dialog_title_wait);
                dialog.setMessage(
                        getString(R.string.user_files_browser_progress_dialog_message_upload_file,
                                path));
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setMax((int) new File(path).length());
                dialog.setCancelable(false);
                dialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            userFileManagerCreatingLatch.await();
                        } catch (final InterruptedException ex) {
                            // This thread should never be interrupted.
                            throw new RuntimeException(ex);
                        }

                        final File file = new File(path);
                        userFileManager.uploadContent(file, file.getName(), new ContentProgressListener() {
                            @Override
                            public void onSuccess(final ContentItem contentItem) {
                                dialog.dismiss();
                                showUploadOk(R.string.user_files_demo_ok_message_upload_file,
                                        file.getName());
                            }

                            @Override
                            public void onProgressUpdate(final String fileName, final boolean isWaiting,
                                                         final long bytesCurrent, final long bytesTotal) {
                                dialog.setProgress((int) bytesCurrent);
                            }

                            @Override
                            public void onError(final String fileName, final Exception ex) {
                                dialog.dismiss();
                                showError(R.string.user_files_browser_error_message_upload_file,
                                        ex.getMessage());
                            }
                        });
                    }
                }).start();
            }
        }
    }

    private void showError(final int resId, Object... args) {
        new AlertDialog.Builder(getActivity()).setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(getString(resId, (Object[]) args))
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void showUploadOk(final int resId, Object... args) {
        new AlertDialog.Builder(getActivity()).setIcon(android.R.drawable.ic_dialog_info)
                .setMessage(getString(resId, (Object[]) args))
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
