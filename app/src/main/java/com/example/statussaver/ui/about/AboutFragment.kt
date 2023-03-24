package com.example.statussaver.ui.about

import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.statussaver.BuildConfig
import com.example.statussaver.R
import com.example.statussaver.databinding.FragmentAboutBinding
import java.io.File


/**
 * A simple [Fragment] subclass.
 * Use the [AboutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AboutFragment : Fragment() {


    companion object {

    }

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        binding.textViewAppVersion.text =
            resources.getString(R.string.app_version, BuildConfig.VERSION_NAME)


        binding.cardViewPrivacy.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))
            startActivity(browserIntent)
        }


        binding.cardViewFeedback.setOnClickListener {
            val Email = Intent(Intent.ACTION_SEND)
            Email.type = "text/email"
            Email.putExtra(Intent.EXTRA_EMAIL, arrayOf("admin@hotmail.com"))
            Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback")
            Email.putExtra(Intent.EXTRA_TEXT, "Dear ...," + "")
            startActivity(Intent.createChooser(Email, "Send Feedback:"))
        }

        binding.cardViewDisclaimer.setOnClickListener {

            val alertDialog = AlertDialog.Builder(
                requireActivity()
            )
            alertDialog.setTitle("Disclaimer")

            alertDialog.setMessage(Html.fromHtml("<p>Status Saver is not partnered with WhatsApp in any way. It is just a tool to save the WhatsApp Status.</p>" +
                   "<p>We don't own the copyrights of any Status. So please download or use Status for commercial purposes by taking respective owner's permission.</p>"
                    ))

            alertDialog.setNegativeButton(
                "OK"
            ) { dialogInterface, i -> dialogInterface.dismiss() }
            alertDialog.show()

        }
        return binding.root
    }

}