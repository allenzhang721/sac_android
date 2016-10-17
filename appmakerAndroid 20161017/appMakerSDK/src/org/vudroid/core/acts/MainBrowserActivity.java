package org.vudroid.core.acts;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;

public class MainBrowserActivity extends BaseBrowserActivity
{
    private final static HashMap<String, Class<? extends Activity>> extensionToActivity = new HashMap<String, Class<? extends Activity>>();

    static
    {
        extensionToActivity.put("pdf", PdfViewerActivity.class);
    }

    @Override
    protected FileFilter createFileFilter()
    {
        return new FileFilter()
        {
            public boolean accept(File pathname)
            {
                for (String s : extensionToActivity.keySet())
                {
                    if (pathname.getName().endsWith("." + s)) return true;
                }
                if(pathname.getName().startsWith("."))
                    return false;
                //if(pathname.getName().endsWith(".tmp")) return false;
                return pathname.isDirectory();
            }
        };
    }

    @Override
    protected void showDocument(Uri uri)
    {
        final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        String uriString = uri.toString();
        String extension = uriString.substring(uriString.lastIndexOf('.') + 1);
        intent.setClass(this, extensionToActivity.get(extension));
        startActivity(intent);
    }
}
