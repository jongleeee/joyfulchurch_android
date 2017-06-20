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
import android.text.SpannableString;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.content.ContentDownloadPolicy;
import com.amazonaws.mobile.content.ContentItem;
import com.amazonaws.mobile.content.ContentListHandler;
import com.amazonaws.mobile.content.ContentManager;
import com.amazonaws.mobile.content.ContentProgressListener;
import com.amazonaws.mobile.content.ContentState;
import com.amazonaws.mobile.content.S3ContentSummary;
import com.amazonaws.mobile.content.UserFileManager;
import com.amazonaws.mobile.util.ImageSelectorUtils;
import com.amazonaws.mobile.util.S3Utils;
import com.amazonaws.mobile.util.StringFormatUtils;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.mysampleapp.R;
import com.mysampleapp.demo.content.ContentListItem;
import com.mysampleapp.demo.content.ContentListViewAdapter;
import com.mysampleapp.demo.content.ContentUtils;
import com.mysampleapp.util.ContentHelper;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class UserFilesBrowserFragment extends DemoFragmentBase
    implements AdapterView.OnItemClickListener {
    /** Log tag. */
    private static final String LOG_TAG = UserFilesBrowserFragment.class.getSimpleName();

    /** Bundle key for retrieving the name of the S3 Bucket. */
    public static final String BUNDLE_ARGS_S3_BUCKET = "bucket";

    /** Bundle key for retrieving the s3 prefix at which to root the content manager. */
    public static final String BUNDLE_ARGS_S3_PREFIX = "prefix";

    /** Bundle key for retrieving the name of the S3 Bucket region. */
    public static final String BUNDLE_ARGS_S3_REGION = "region";

    /** Permission Request Code (Must be < 256). */
    private static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 93;

    private ContentListViewAdapter contentListItems;

    /** The list view for displaying the list of files. */
    private ListView listView;

    /** The s3 bucket. */
    private String bucket;

    /** The S3 bucket region. */
    private Regions region;

    /** The s3 Prefix at which the UserFileManager is rooted. */
    private String prefix;

    /** The current relative path within the UserFileManager. */
    private String currentPath = "";

    /** The user file manager. */
    private UserFileManager userFileManager;

    /** Text view for showing the current path. */
    private TextView pathTextView;

    /** Text view for showing the maximum number of bytes for the cache. */
    private TextView cacheLimitTextView;

    /** Text view for showing the number of bytes in use in the cache. */
    private TextView cacheInUseTextView;

    /** Text view for showing the number of bytes available in the cache. */
    private TextView cacheAvailableTextView;

    /** Text view for showing the number of bytes pinned in the cache. */
    private TextView cachePinnedTextView;

    /** Menu Text for refreshing content. */
    private String refreshText;

    /** Menu Text for downloading recent content. */
    private String downloadRecentText;

    /** Menu Text for setting the cache size. */
    private String setCacheSizeText;

    /** Menu Text for clearing the cache. */
    private String clearCacheText;

    /** Menu Text for uploading a file. */
    private String uploadText;

    /** Menu Text for creating a new folder. */
    private String newFolderText;

    /** Flag to keep track of whether currently listing content. */
    private boolean listingContentInProgress = false;

    private final CountDownLatch userFileManagerCreatingLatch = new CountDownLatch(1);

    private void createContentList(final View fragmentView, final ContentManager contentManager) {
        listView = (ListView) fragmentView.findViewById(android.R.id.list);
        contentListItems = new ContentListViewAdapter(fragmentView.getContext(), contentManager,
            new ContentListViewAdapter.ContentListPathProvider() {
                @Override
                public String getCurrentPath() {
                    return currentPath;
                }
            },
            new ContentListViewAdapter.ContentListCacheObserver() {
                @Override
                public void onCacheChanged() {
                    refreshCacheSummary();
                }
            },
            R.layout.fragment_demo_user_files_browser);
        listView.setAdapter(contentListItems);
        listView.setOnItemClickListener(this);
        listView.setOnCreateContextMenuListener(this);
    }

    private void updatePath() {
        pathTextView.setText(getString(R.string.content_path_prefix_text)
            + (currentPath.isEmpty() ? "./" : currentPath));
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshText = getString(R.string.content_refresh_text);
        downloadRecentText = getString(R.string.content_download_recent_text);
        setCacheSizeText = getString(R.string.content_set_cache_size_text);
        clearCacheText = getString(R.string.content_clear_cache_text);
        uploadText = getString(R.string.user_files_browser_upload_text);
        newFolderText = getString(R.string.user_files_browser_new_folder_text);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View fragmentView = inflater.inflate(R.layout.fragment_demo_user_files_browser, container, false);

        pathTextView = (TextView) fragmentView.findViewById(R.id.content_path);
        cacheLimitTextView = (TextView) fragmentView.findViewById(R.id.content_cache_limit_value);
        cacheInUseTextView = (TextView) fragmentView.findViewById(R.id.content_cache_use_value);
        cacheAvailableTextView = (TextView) fragmentView.findViewById(R.id.content_cache_available_value);
        cachePinnedTextView = (TextView) fragmentView.findViewById(R.id.content_cache_pinned_value);

        registerForContextMenu(cacheLimitTextView);

        return fragmentView;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Bundle args = getArguments();
        bucket = args.getString(BUNDLE_ARGS_S3_BUCKET);
        prefix = args.getString(BUNDLE_ARGS_S3_PREFIX);
        region = Regions.fromName(args.getString(BUNDLE_ARGS_S3_REGION));
        currentPath = "";

        // When a sign-in user open protected folder option, it goes straight to their own protected folder
        // If a Guest user open protected folder goes to protected root folder
        if(UserFilesDemoFragment.S3_PREFIX_PROTECTED.equalsIgnoreCase(prefix)){
            final boolean isUserSignedIn = AWSMobileClient.defaultMobileClient()
                    .getIdentityManager()
                    .isUserSignedIn();

            if(isUserSignedIn){
                final String identityId = AWSMobileClient.defaultMobileClient()
                        .getIdentityManager()
                        .getCachedUserID();
                currentPath = identityId + "/";
            }
        }


        final ProgressDialog dialog = getProgressDialog(
                R.string.content_progress_dialog_message_load_local_content);

        AWSMobileClient.defaultMobileClient()
            .createUserFileManager(this.getContext().getApplicationContext(), bucket, prefix, region,
                new UserFileManager.BuilderResultHandler() {
                    @Override
                    public void onComplete(final UserFileManager userFileManager) {
                        if (!isAdded()) {
                            userFileManager.destroy();
                            return;
                        }

                        UserFilesBrowserFragment.this.userFileManager = userFileManager;
                        createContentList(getView(), userFileManager);
                        userFileManager.setContentRemovedListener(contentListItems);
                        userFileManagerCreatingLatch.countDown();
                        dialog.dismiss();
                        refreshContent(currentPath);
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (userFileManager != null) {
            // Remove any Progress listeners that may be registered.
            userFileManager.clearAllListeners();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (userFileManager != null) {
            userFileManager.setContentRemovedListener(contentListItems);
            refreshContent(currentPath);
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {

        menu.add(0, R.id.content_context_menu_refresh,
                0, refreshText);
        menu.add(0, R.id.content_context_menu_download_recent,
                0, downloadRecentText);
        menu.add(0, R.id.content_context_menu_set_cache_size,
                0, setCacheSizeText);
        menu.add(0, R.id.content_context_menu_clear_cache,
                0, clearCacheText);
        menu.add(0, R.id.user_files_context_menu_upload,
                0, uploadText);
        menu.add(0, R.id.user_files_context_menu_new_folder,
                0, newFolderText);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch(item.getItemId()) {
            case R.id.content_context_menu_refresh:
                refreshContent(currentPath);
                return true;
            case R.id.content_context_menu_download_recent:
                userFileManager.downloadRecentContent(currentPath, contentListItems);
                return true;
            case R.id.content_context_menu_set_cache_size:
                final View fragmentView = getView();
                if (fragmentView != null) {
                    // Show selector for cache size.
                    getActivity().openContextMenu(cacheLimitTextView);
                }
                return true;
            case R.id.content_context_menu_clear_cache:
                userFileManager.clearCache();
                return true;
            case R.id.user_files_context_menu_upload:
                final Activity activity = getActivity();
                if (activity == null) {
                    return true;
                }

                if (ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
                    return true;
                }

                // We have permission, so show the image selector.
                final Intent intent = ImageSelectorUtils.getImageSelectionIntent();
                startActivityForResult(intent, 0);
                return true;
            case R.id.user_files_context_menu_new_folder:
                final EditText txtFolder = new EditText(getActivity());
                txtFolder.setSingleLine(true);
                new AlertDialog.Builder(getActivity()).setTitle(
                        R.string.user_files_browser_dialog_create_folder_title)
                        .setView(txtFolder)
                        .setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog,
                                                        final int which) {
                                        final String folderKey = currentPath + txtFolder.getText()
                                                .toString() + "/";
                                        createFolder(folderKey);
                                    }
                                })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           final String permissions[], final int[] grantResults) {

        if (requestCode == EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                final Intent intent = ImageSelectorUtils.getImageSelectionIntent();
                startActivityForResult(intent, 0);
            } else {
                // Inform the user they won't be able to upload without permission.
                final Activity activity = getActivity();
                if (activity != null) {
                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                    dialogBuilder.setTitle(activity.getString(R.string.content_permission_failure_title_text));
                    dialogBuilder.setMessage(activity.getString(R.string.content_permission_failure_text));
                    dialogBuilder.setNegativeButton(
                        activity.getString(android.R.string.ok), null);
                    dialogBuilder.show();
                }
            }
        }
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View view,
                                    final ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        if (view == cacheLimitTextView) {
            for (final SpannableString string : ContentUtils.cacheSizeStrings) {
                menu.add(string).setActionView(view);
            }
            menu.setHeaderTitle(ContentUtils.getCenteredString(setCacheSizeText));
        } else if (view.getId() == listView.getId()) {
            final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            final ContentItem content = contentListItems.getItem(info.position).getContentItem();

            if (ContentState.REMOTE_DIRECTORY.equals(content.getContentState())) {
                menu.add(R.string.user_files_browser_context_menu_delete).setActionView(view);
            } else {
                final ContentState contentState = content.getContentState();
                final boolean isNewerVersionAvailable =
                    ContentState.isCachedWithNewerVersionAvailableOrTransferring(contentState);
                final boolean isCached = contentState == ContentState.CACHED || isNewerVersionAvailable;
                final boolean isPinned = userFileManager.isContentPinned(content.getFilePath());

                // if item is downloaded
                if (isCached) {
                    menu.add(0,
                            R.id.content_context_menu_open,
                            1, // menu item order
                            getString(R.string.content_context_menu_open)).setActionView(view);
                    menu.add(0,
                            R.id.content_context_menu_delete_local,
                            8, // menu item order
                            getString(R.string.content_context_menu_delete_local)).setActionView(view);
                    if (!isPinned) {
                        menu.add(0,
                                R.id.content_context_menu_pin,
                                5, // menu item order
                                getString(R.string.content_context_menu_pin)).setActionView(view);
                    }
                } else {
                    menu.add(0,
                            R.id.content_context_menu_open_remote,
                            2, // menu item order
                            R.string.content_context_menu_open_remote).setActionView(view);
                    menu.add(0,
                            R.id.content_context_menu_download,
                            3, // menu item order
                            getString(R.string.content_context_menu_download)).setActionView(view);
                    menu.add(0,
                            R.id.content_context_menu_download_pin,
                            6, // menu item order
                            getString(R.string.content_context_menu_download_pin)).setActionView(view);
                }
                if (isNewerVersionAvailable) {
                    menu.add(0,
                            R.id.content_context_menu_download_latest,
                            4, // menu item order
                            getString(R.string.content_context_menu_download_latest)).setActionView(view);
                }
                if (isPinned) {
                    menu.add(0,
                            R.id.content_context_menu_unpin,
                            7, // menu item order
                            getString(R.string.content_context_menu_unpin)).setActionView(view);
                }
                menu.add(0,
                        R.id.user_files_context_menu_delete,
                        9, // menu item order
                        R.string.user_files_browser_context_menu_delete).setActionView(view);
            }
            menu.setHeaderTitle(content.getFilePath());
        }
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        if (item.getActionView() == cacheLimitTextView) {
            final SpannableString itemTitle = (SpannableString) item.getTitle();
            final int cacheSizeIndex = ContentUtils.cacheSizeStrings.indexOf(itemTitle);
            if (cacheSizeIndex > -1) {
                userFileManager.setContentCacheSize(ContentUtils.cacheSizeValues[cacheSizeIndex]);
                refreshCacheSummary();
            }
            return true;
        } else if (item.getActionView() == listView) {
            final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            final ContentItem content = contentListItems.getItem(info.position).getContentItem();

            switch(item.getItemId()) {
                case R.id.content_context_menu_open:
                    ContentHelper.openFileViewer(getActivity(), content.getFile());
                    return true;
                case R.id.content_context_menu_open_remote:
                    // Open the object in a browser via a presigned URL.
                    final AmazonS3 s3 =
                            new AmazonS3Client(AWSMobileClient.defaultMobileClient().getIdentityManager().getCredentialsProvider());
                    s3.setRegion(Region.getRegion(region));

                    final URL presignedUrl = s3.generatePresignedUrl(bucket, prefix + content.getFilePath(),
                            new Date(new Date().getTime() + 60 * 60 * 1000));
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(presignedUrl.toString()));
                    startActivity(intent);
                    return true;
                case R.id.content_context_menu_download:
                    // Download the content.
                    userFileManager.getContent(content.getFilePath(), content.getSize(),
                            ContentDownloadPolicy.DOWNLOAD_ALWAYS, false, contentListItems);
                    return true;
                case R.id.content_context_menu_download_latest:
                    // Download the content.
                    userFileManager.getContent(content.getFilePath(), content.getSize(),
                            ContentDownloadPolicy.DOWNLOAD_IF_NEWER_EXIST, false, contentListItems);
                    return true;
                case R.id.content_context_menu_pin:
                    userFileManager.pinContent(content.getFilePath(), contentListItems);
                    return true;
                case R.id.content_context_menu_download_pin:
                    userFileManager.getContent(content.getFilePath(), content.getSize(),
                            ContentDownloadPolicy.DOWNLOAD_ALWAYS, true, contentListItems);
                    return true;
                case R.id.content_context_menu_unpin:
                    userFileManager.unPinContent(content.getFilePath(), new Runnable() {
                        @Override
                        public void run() {
                            refreshCacheSummary();
                            contentListItems.notifyDataSetChanged();
                        }
                    });
                    return true;
                case R.id.content_context_menu_delete_local:
                    userFileManager.removeLocal(content.getFilePath());
                    return true;
                case R.id.user_files_context_menu_delete:
                    deleteItem(info.position);
                    return true;
            }
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        unregisterForContextMenu(cacheLimitTextView);

        if (userFileManager != null) {
            userFileManager.destroy();
        }

        super.onDestroyView();
    }

    private void changeDirectory(final String directory) {
        final String newPath;
        // change directory
        if (directory.equals(currentPath)) {
            // up one level
            newPath = S3Utils.getParentDirectory(directory);
        } else {
            newPath = directory;
        }
        // update content manager with new prefix
        refreshContent(newPath);
    }

    private void createFolder(final String folderKey) {
        final ProgressDialog dialog = getProgressDialog(
                R.string.user_files_browser_progress_dialog_message_create_folder, folderKey);

        userFileManager.createFolder(folderKey, new UserFileManager.ResponseHandler() {
            @Override
            public void onSuccess() {
                dialog.dismiss();
                contentListItems.add(new ContentListItem(new S3ContentSummary(folderKey)));
                contentListItems.sort(ContentListItem.contentAlphebeticalComparator);
                contentListItems.notifyDataSetChanged();
            }

            @Override
            public void onError(final AmazonClientException ace) {
                dialog.dismiss();
                Log.e(LOG_TAG, "Failed to create folder " + folderKey, ace);
                showError(R.string.user_files_browser_error_message_create_folder, ace.getMessage());
            }
        });
    }

    private void deleteItem(final int position) {
        final ContentListItem listItem = contentListItems.getItem(position);
        final ContentItem content = listItem.getContentItem();
        final ProgressDialog dialog = getProgressDialog(
                R.string.user_files_browser_progress_dialog_message_delete_item, content.getFilePath());

        userFileManager.deleteRemoteContent(content.getFilePath(), new UserFileManager.ResponseHandler() {
            @Override
            public void onSuccess() {
                dialog.dismiss();
                // Remove from the list if not cached locally.
                if (!ContentState.isCached(content.getContentState())) {
                    contentListItems.remove(listItem);
                    contentListItems.notifyDataSetChanged();
                } else {
                    content.setContentState(ContentState.CACHED);
                }
            }

            @Override
            public void onError(final AmazonClientException ex) {
                dialog.dismiss();
                showError(R.string.user_files_browser_error_message_delete_item, ex.getMessage());
            }
        });
    }

    private void showError(final int resId, Object... args) {
        new AlertDialog.Builder(getActivity()).setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(getString(resId, (Object[]) args))
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private ProgressDialog getProgressDialog(final int resId, Object... args) {
        return ProgressDialog.show(getActivity(),
                getString(R.string.content_progress_dialog_title_wait),
                getString(resId, (Object[]) args));
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
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
                    userFileManager.uploadContent(file, currentPath + file.getName(), new ContentProgressListener() {
                        @Override
                        public void onSuccess(final ContentItem contentItem) {
                            contentListItems.add(new ContentListItem(contentItem));
                            contentListItems.sort(ContentListItem.contentAlphebeticalComparator);
                            contentListItems.notifyDataSetChanged();
                            dialog.dismiss();
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

    private void refreshCacheSummary() {
        // Load the item selected from the current cache size.
        final long limitBytes = userFileManager.getContentCacheSize();
        final long usedBytes = userFileManager.getCacheUsedSize();
        final long pinnedBytes = userFileManager.getPinnedSize();

        cacheLimitTextView.setText(StringFormatUtils.getBytesString(limitBytes, false));
        cacheInUseTextView.setText(StringFormatUtils.getBytesString(usedBytes, false));
        cacheAvailableTextView.setText(StringFormatUtils.getBytesString(
            limitBytes - usedBytes, false));
        cachePinnedTextView.setText(StringFormatUtils.getBytesString(pinnedBytes, false));
    }

    private void refreshContent(final String newCurrentPath) {
        if (!listingContentInProgress) {
            listingContentInProgress = true;

            refreshCacheSummary();

            // Remove all progress listeners.
            userFileManager.clearProgressListeners();

            // Clear old content.
            contentListItems.clear();
            contentListItems.notifyDataSetChanged();

            currentPath = newCurrentPath;
            updatePath();

            final ProgressDialog dialog = getProgressDialog(
                R.string.content_progress_dialog_message_load_content);

            userFileManager.listAvailableContent(newCurrentPath, new ContentListHandler() {
                @Override
                public boolean onContentReceived(final int startIndex,
                                                 final List<ContentItem> partialResults,
                                                 final boolean hasMoreResults) {
                    // if the activity is no longer alive, we can stop immediately.
                    if (getActivity() == null) {
                        listingContentInProgress = false;
                        return false;
                    }
                    if (startIndex == 0) {
                        dialog.dismiss();
                    }

                    for (final ContentItem contentItem : partialResults) {
                        // Add the item to the list.
                        contentListItems.add(new ContentListItem(contentItem));

                        // If the content is transferring, ensure the progress listener is set.
                        final ContentState contentState = contentItem.getContentState();
                        if (ContentState.isTransferringOrWaitingToTransfer(contentState)) {
                            userFileManager.setProgressListener(contentItem.getFilePath(),
                                    contentListItems);
                        }
                    }
                    contentListItems.sort(ContentListItem.contentAlphebeticalComparator);

                    if (!hasMoreResults) {
                        listingContentInProgress = false;
                    }

                    // When a sign-in-user get into their own protected folder for the first time
                    // It creates that folder automatically
                    if(UserFilesDemoFragment.S3_PREFIX_PROTECTED.equals(prefix) &&
                            contentListItems.isEmpty()){
                        final boolean isUserSignedIn = AWSMobileClient.defaultMobileClient()
                                .getIdentityManager()
                                .isUserSignedIn();

                        if (isUserSignedIn) {
                            createFolder(currentPath);
                        }
                    }

                    // Return true to continue listing.
                    return true;
                }

                @Override
                public void onError(final Exception ex) {
                    dialog.dismiss();
                    listingContentInProgress = false;
                    final Activity activity = getActivity();
                    if (activity != null) {
                        final AlertDialog.Builder errorDialogBuilder = new AlertDialog.Builder(activity);
                        errorDialogBuilder.setTitle(activity.getString(R.string.content_list_failure_text));
                        errorDialogBuilder.setMessage(ex.getMessage());
                        errorDialogBuilder.setNegativeButton(
                                activity.getString(R.string.content_dialog_ok), null);
                        errorDialogBuilder.show();
                    }
                }
            });

        }
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        final ContentItem content = contentListItems.getItem(position).getContentItem();
        if (content.getContentState().equals(ContentState.REMOTE_DIRECTORY)) {
            changeDirectory(content.getFilePath());
        } else {
            listView.showContextMenuForChild(view);
        }
    }
}
