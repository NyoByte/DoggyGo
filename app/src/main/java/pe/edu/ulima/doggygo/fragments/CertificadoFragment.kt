package pe.edu.ulima.doggygo.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import pe.edu.ulima.doggygo.R
import pe.edu.ulima.doggygo.adapter.PdfAdapter
import pe.edu.ulima.doggygo.manager.UserManager
import pe.edu.ulima.doggygo.model.DogWalker
import pe.edu.ulima.doggygo.model.User
import java.io.File

class CertificadoFragment: Fragment() {

    private lateinit var progressBar: ProgressBar
    private lateinit var pdfView: PDFView

    private val database = Firebase.database
    private val myRef = database.getReference("user")

    private val fileResult = 1

    private var user: User? = null

    private lateinit var pdfName: String

    private lateinit var userManager:UserManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        user = arguments?.getSerializable("user") as User
        return inflater.inflate(R.layout.fragment_certificado, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pdfView = view.findViewById(R.id.pdfView)
        progressBar = view.findViewById(R.id.progressBar)
        pdfName = user?.id!!
        userManager =  UserManager(requireActivity().applicationContext)

        fileDownload(pdfName) { urlPdf ->
            progressBar.visibility = View.VISIBLE
            pdfView.background = null
            if(urlPdf != null) {
                PdfAdapter(pdfView, progressBar, context).execute(urlPdf)
            }else{
                progressBar.visibility = View.GONE
                pdfView.setBackgroundResource(R.drawable.ic_baseline_picture_as_pdf_24)
            }
        }

        view.findViewById<Button>(R.id.btnSubirPdf).setOnClickListener {
            fileManager()
            progressBar.visibility = View.VISIBLE
            pdfView.background = null
        }
    }

    private fun fileManager(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        startActivityForResult(intent, fileResult)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == fileResult){
            if(resultCode == RESULT_OK && data!= null){
                val clipData = data.clipData
                if(clipData!=null){
                    for(i in 0 until clipData.itemCount){
                        val uri = clipData.getItemAt(i).uri
                        uri?.let {fileUpload(it)}
                    }
                }else{
                    val uri = data.data
                    uri?.let {fileUpload(it)}
                }
            }else{
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun fileDownload(pdfName:String, callbackOK: (String?) -> Unit){
        val storageRef: StorageReference = FirebaseStorage.getInstance().reference.child("Certificates/${pdfName}.pdf")
        val localfile = File.createTempFile("tempPdf","pdf")
        storageRef.getFile(localfile)
            .addOnSuccessListener{
                storageRef.downloadUrl.addOnCompleteListener{
                    callbackOK(it.result.toString())
                }
            }
            .addOnFailureListener{
                callbackOK(null)
                Toast.makeText(requireContext(),"Falló la carga del pdf",Toast.LENGTH_SHORT).show()
            }
    }

    private fun fileUpload(myUri:Uri){
        val folder: StorageReference = FirebaseStorage.getInstance().reference.child("Certificates")
        //val path = myUri.lastPathSegment.toString()
        //val fileName: StorageReference = folder.child(path.substring(path.lastIndexOf('/')+1))
        val fileName: StorageReference = folder.child("${pdfName}.pdf")
        fileName.putFile(myUri)
            .addOnSuccessListener{
                fileName.downloadUrl.addOnSuccessListener{ uri ->
                    PdfAdapter(pdfView, progressBar, context).execute(uri.toString())
                    userManager.updateCertificate(user?.id!!,false)
                    Log.i("CertificadoFragment", "File upload successfully")
                }
            }
            .addOnFailureListener {
                Log.i("CertificadoFragment","File upload error")
            }
    }


}