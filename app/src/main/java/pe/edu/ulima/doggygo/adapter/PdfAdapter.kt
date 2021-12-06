package pe.edu.ulima.doggygo.adapter
import android.content.Context
import android.os.AsyncTask
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.github.barteksc.pdfviewer.PDFView
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class PdfAdapter(private val pdfView:PDFView, private val progressBar:ProgressBar, private val context: Context?) : AsyncTask<String, Void, InputStream>(){

    override fun doInBackground(vararg p0: String?): InputStream? {
        var inputStream: InputStream? = null
        try {
            val url = URL(p0[0])
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            if (urlConnection.responseCode == 200) {
                inputStream = BufferedInputStream(urlConnection.inputStream)
            }
        } catch (e: IOException) {
            return null
        }
        return inputStream
    }
    override fun onPostExecute(result: InputStream?) {
        super.onPostExecute(result)
        if (result != null) {
            pdfView.fromStream(result)
                .swipeHorizontal(true)
                .load()
            progressBar.visibility = View.GONE
        }else{
            progressBar.visibility = View.GONE
            Toast.makeText(context, "Error obtaining certificate", Toast.LENGTH_SHORT).show()
        }
    }
}