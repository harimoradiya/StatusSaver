package com.status.statussaver.ui.frags.chat

import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.hbb20.CountryCodePicker
import com.status.saver.video.R
import com.status.saver.video.databinding.FragmentKeypadBinding
import java.io.UnsupportedEncodingException
import java.util.regex.Matcher
import java.util.regex.Pattern


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [KeypadFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class KeypadFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private val TAG: String = "KeypadFragment"
    var f3723r0 = ""


    private var _binding: FragmentKeypadBinding? = null
    private val binding get() = _binding!!
    private lateinit var editQuery: AppCompatEditText
    private lateinit var ccp: CountryCodePicker

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKeypadBinding.inflate(inflater, container, false)

        editQuery = binding.editQuery
        ccp = binding.ccp


        binding.mbOne.setOnClickListener {
            view?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            editQuery.text?.insert(editQuery.selectionStart, "1");

        }
        binding.mbTwo.setOnClickListener {
            view?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            editQuery.text?.insert(editQuery.selectionStart, "2");
        }
        binding.mbThree.setOnClickListener {
            view?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            editQuery.text?.insert(editQuery.selectionStart, "3");
        }
        binding.mbFour.setOnClickListener {
            view?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            editQuery.text?.insert(editQuery.selectionStart, "4");
        }
        binding.mbFive.setOnClickListener {
            view?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            editQuery.text?.insert(editQuery.selectionStart, "5");
        }
        binding.mbSix.setOnClickListener {
            view?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            editQuery.text?.insert(editQuery.selectionStart, "6");
        }
        binding.mbSeven.setOnClickListener {
            view?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            editQuery.text?.insert(editQuery.selectionStart, "7");
        }
        binding.mbEight.setOnClickListener {
            view?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            editQuery.text?.insert(editQuery.selectionStart, "8");
        }
        binding.mbNine.setOnClickListener {
            view?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            editQuery.text?.insert(editQuery.selectionStart, "9");
        }
        binding.mbZero.setOnClickListener {
            view?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            editQuery.text?.insert(editQuery.selectionStart, "0");
        }
        binding.mbCopy.setOnClickListener {
            view?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            pasteContent()
        }
        binding.mbBackspace.setOnClickListener {
            view?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            val selectionStart: Int = editQuery.selectionStart
            editQuery.text?.delete(0.coerceAtLeast(selectionStart - 1), selectionStart)

        }

            binding.mbChat.setOnClickListener {

                if (editQuery.text.toString().length > 6) {
                    view?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    (requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
                        ClipData.newPlainText("label", editQuery.text)
                    )


                    val parse =
                        Uri.parse(
                            "smsto:" + ccp.selectedCountryCodeWithPlus + " " + editQuery.text.toString()
                                .trim() as Any
                        )
                  val   intent = Intent("android.intent.action.SENDTO", parse)
                    startActivity(intent)

                    return@setOnClickListener
                }
                Snackbar.make(
                    binding.scrollView, "Please enter valid phone number",
                    Snackbar.LENGTH_SHORT
                )
                    .show()


            }
        binding.mbWhatsapp.setOnClickListener {


            if (!isAppInstalled(requireContext(), "com.whatsapp")) {
                Snackbar.make(
                    binding.scrollView, "Please Install WhatsApp",
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            } else {
                if (TextUtils.isEmpty(binding.editQuery.text.toString())) {
                    Snackbar.make(
                        binding.scrollView, "Please enter valid phone number",
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                    return@setOnClickListener
                } else if (TextUtils.isEmpty(binding.ccp.selectedCountryCodeWithPlus.toString())) {
                    Snackbar.make(
                        binding.scrollView, "Select country code first",
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                    return@setOnClickListener
                } else {
                    view?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    sendMessage()
                    return@setOnClickListener
                }
            }

        }
        binding.mbSend.setOnClickListener {
            if (editQuery.getText().toString().length > 6) {
                view?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                (this@KeypadFragment.activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
                    ClipData.newPlainText("label", editQuery.text)
                )
                var defaultCountryCodeWithPlus: String = ccp.selectedCountryCodeWithPlus
                Log.e(TAG, "YOur number:$defaultCountryCodeWithPlus")
                var str3 = ""
                try {
                    str3 =
                        defaultCountryCodeWithPlus + " " + editQuery.text.toString().trim() as Any
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
              val  intent = Intent("android.intent.action.SEND")
                intent.type = "text/plain"
                intent.putExtra(
                    "android.intent.extra.SUBJECT",
                    R.string.app_name
                )
                intent.putExtra("android.intent.extra.TEXT", str3)
                startActivity(intent)
                return@setOnClickListener
            }
            Snackbar.make(
                binding.scrollView, "Please enter valid phone number",
                Snackbar.LENGTH_SHORT
            )
                .show()


        }

        binding.mbBackspace.setOnLongClickListener {
            editQuery.setText(" ", TextView.BufferType.NORMAL);
            return@setOnLongClickListener false
        }
        editQuery.showSoftInputOnFocus = false;
        disableSoftInputFromAppearing(editQuery)
        return binding.root

    }

    fun disableSoftInputFromAppearing(editText: EditText) {
        if (Build.VERSION.SDK_INT >= 11) {
            editText.setRawInputType(InputType.TYPE_CLASS_TEXT)
            editText.setTextIsSelectable(true)
        } else {
            editText.setRawInputType(InputType.TYPE_NULL)
            editText.isFocusable = true
        }
    }

    fun isAppInstalled(context: Context, packageName: String?): Boolean {
        return try {
            context.packageManager.getApplicationInfo(packageName!!, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }


    private fun pasteContent() {
        try {
            val clipboard =
                requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?


            val charSequence: String =
                clipboard?.primaryClip
                    ?.getItemAt(0)?.text.toString()
            Log.e(TAG, "pasteContent:$charSequence")
            val compile: Pattern = Pattern.compile("[a-zA-z]")
            val compile2: Pattern = Pattern.compile("[!@#$%&*_=|<>?{}\\[\\]~]")
            val matcher: Matcher = compile.matcher(charSequence)
            val matcher2: Matcher = compile2.matcher(charSequence)
            if (!matcher.find() && !matcher2.find()) {
                var sb = StringBuilder()
                var defaultCountryCodeWithPlus: String = ccp.selectedCountryCodeWithPlus
                Log.e(TAG, "copied number:$charSequence")
                if (charSequence.contains("+")) {
                    val split = charSequence.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()

                    Log.e("size of number", split.size.toString())
                    if (split.size > 1) {
                        defaultCountryCodeWithPlus = split[0]
                        for (i in 1 until split.size) {
                            sb.append(split[i])
                        }
                    } else if (charSequence.trim { it <= ' ' }.length > 5) {
                        sb.append(charSequence.replace("+", "").replace(" ", "").replace("  ", ""))
                    } else {
                        sb = StringBuilder()
                    }
                } else {

                    sb = StringBuilder(charSequence.replace(" ", "").replace("  ", ""))
                }

                editQuery.setText("")
                editQuery.text?.insert(editQuery.selectionStart, sb)
                Log.e(TAG, "CountryCode:$defaultCountryCodeWithPlus")
                ccp.setCountryForPhoneCode(
                    defaultCountryCodeWithPlus.replace("+", "").toInt()
                )
                return
            }
            val countryCodePicker: CountryCodePicker = this.ccp
            countryCodePicker.setCountryForPhoneCode(
                countryCodePicker.defaultCountryCodeWithPlus.replace("+", "").toInt()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            val countryCodePicker2: CountryCodePicker = this.ccp
            countryCodePicker2.setCountryForPhoneCode(
                countryCodePicker2.defaultCountryCodeWithPlus.replace("+", "").toInt()
            )
        }
    }

    fun sendMessage() {
        try {
            val intent = Intent("android.intent.action.VIEW")


            val defaultCountryCodeWithPlus: String =
                ccp.selectedCountryCodeWithPlus

            val numberFromEditText = editQuery.text.toString()
            val re = Regex("[()-]")
            val finalNumber = re.replace(numberFromEditText, "") // works
//            println(answer)


            Log.e(
                TAG,
                "http://api.whatsapp.com/send?phone=$defaultCountryCodeWithPlus" + finalNumber
            )
            intent.data = Uri.parse(
                "http://api.whatsapp.com/send?phone=" + defaultCountryCodeWithPlus + finalNumber +
                        "&text="
            )
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("e", e.message.toString())
        }
    }

    companion object {

    }


    override fun onResume() {
        super.onResume()
//           pasteContent()
    }


//    override fun onPause() {
//        super.onPause()
//        (requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
//            ClipData.newPlainText(
//                "label",
//                this.ccp.selectedCountryCodeWithPlus + " " + this.editQuery.text
//                    .toString().trim()
//            )
//
//        )
//        Log.e(TAG,"CountryCode: "+ccp.selectedCountryCodeWithPlus+"Mobile number: "+editQuery.text.toString().trim())
//    }
//
//
//    override fun onDestroy() {
//        super.onDestroy()
//        (requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
//            ClipData.newPlainText(
//                "label",
//                this.ccp.selectedCountryCodeWithPlus + " " + this.editQuery.text
//                    .toString().trim()
//            )
//        )
//        Log.e(TAG,"CountryCode: "+ccp.selectedCountryCodeWithPlus+"Mobile number: "+editQuery.text.toString().trim())
//    }

}