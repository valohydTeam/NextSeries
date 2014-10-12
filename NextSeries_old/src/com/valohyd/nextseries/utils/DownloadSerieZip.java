package com.valohyd.nextseries.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import android.content.Context;
import android.util.Log;

public class DownloadSerieZip {

    private static Document document;
    private static Element racine;
    //private File file = null;

    public Element downloadSerieInfos(Context c, String url, int nb) {
        long time = System.currentTimeMillis();
        racine = null;
        FileOutputStream fileOutput = null;
        while (racine == null) {
            Log.d("NextSeries", "racine null");
            try {

                URL fileUrl = new URL(url);
                URLConnection urlConn = fileUrl.openConnection();
                ZipInputStream zipStream = new ZipInputStream(urlConn.getInputStream());
                ZipEntry entry;
                File SDCardRoot = c.getCacheDir();
                // create a new file, specifying the path, and the filename
                // which we want to save the file as.
                File file = new File(SDCardRoot, "NS_tmp"+nb+".xml");
                // this will be used to write the downloaded data into the file
                // we
                // created
                fileOutput = new FileOutputStream(file);
                time = System.currentTimeMillis() - time;
                Log.d("ZIP", "Avant " + time);
                Log.d("ZIP", "nombre " + nb);
                while ((entry = zipStream.getNextEntry()) != null) {
                    if (entry.getName().equals("fr.xml")) {
                        time = System.currentTimeMillis() - time;
                        Log.d("ZIP", "Debut ecriture " + time);
                        // create a buffer...
                        byte[] buffer = new byte[1024];
                        int bufferLength = 0; // used to store a temporary size
                                              // of
                                              // the buffer

                        // now, read through the input buffer and write the
                        // contents
                        // to the file
                        while ((bufferLength = zipStream.read(buffer)) > 0) {
                            // add the data in the buffer to the file in the
                            // file
                            // output stream (the file on the sd card
                            fileOutput.write(buffer, 0, bufferLength);
                        }
                        time = System.currentTimeMillis() - time;
                        Log.d("ZIP", "Fin ecriture " + time);
                    }
                }
                fileOutput.close();
                SAXBuilder sxb = new SAXBuilder();
                document = sxb.build(file);
                racine = document.getRootElement();
                
                if(file != null) 
                    file.delete();
            } catch (MalformedURLException e) {
                Log.e("ERROR", "URL mal formée : "+e);
                if(fileOutput != null)
                    try {
                        fileOutput.close();
                    } catch (IOException e1) {
                        Log.e("ERROR", "exception a la fermeture : "+e);
                    }
            } catch (IOException e) {
                Log.e("ERROR", "Unzip Error : "+e);
                if(fileOutput != null)
                    try {
                        fileOutput.close();
                    } catch (IOException e1) {
                        Log.e("ERROR", "exception a la fermeture : "+e);
                    }
            } catch (JDOMException e) {
                Log.e("ERROR", "XML Parsing Error : "+e);
                if(fileOutput != null)
                    try {
                        fileOutput.close();
                    } catch (IOException e1) {
                        Log.e("ERROR", "exception a la fermeture : "+e);
                    }
            }
            if (racine == null)
                Log.d("ZIP", "ReDL");
            // downloadSerieInfos(c, url);
        }
        time = System.currentTimeMillis() - time;
        Log.d("DL", "FINI " + time);
        return racine;
    }

    public Element downloadSerieStatusAndCo(Context context, String url, int nb) {
        long time = System.currentTimeMillis();
        racine = null;
        FileOutputStream fileOutput = null;
        int cpt = 0;
        while (racine == null && cpt < 10) {
            cpt++;
            Log.d("NextSeries", "racine null");
            try {
                // télécharger le fichier
                URL fileUrl = new URL(url);
                URLConnection urlConn = fileUrl.openConnection();
                
                // créer le fichier SAX correspondant
                SAXBuilder sxb = new SAXBuilder();
                document = sxb.build(urlConn.getInputStream());
                racine = document.getRootElement();
            } catch (IOException e) {
                Log.e("ERROR", "Unzip Error : "+e);
                if(fileOutput != null)
                    try {
                        fileOutput.close();
                    } catch (IOException e1) {
                        Log.e("ERROR", "exception a la fermeture : "+e);
                    }
            } catch (JDOMException e) {
                Log.e("ERROR", "XML Parsing Error : "+e);
                if(fileOutput != null)
                    try {
                        fileOutput.close();
                    } catch (IOException e1) {
                        Log.e("ERROR", "exception a la fermeture : "+e);
                    }
            }
            if (racine == null)
                Log.d("DB", "ReDL");
            // downloadSerieInfos(c, url);
        }
        return racine;
    }
}

