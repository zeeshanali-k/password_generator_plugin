package app.keyboardly.addon.passwordgenerator.views

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import app.keyboardly.addon.passwordgenerator.databinding.PasswordGeneratorViewBinding
import app.keyboardly.lib.KeyboardActionDependency
import app.keyboardly.lib.KeyboardActionView
import app.keyboardly.lib.helper.InputPresenter
import java.security.SecureRandom

class PasswordGeneratorView(
    dependency: KeyboardActionDependency
) : KeyboardActionView(dependency), InputPresenter {

    // Default values
    private val defaultUseUppercase = true
    private val defaultUseLowercase = true
    private val defaultUseNumber = true
    private val defaultUseSpecialCharacter = true
    private val defaultMinimumNumber = 1
    private val defaultMinimumSpecialCharacters = 1
    private val defaultPasswordCount = 10


    //fields
    private var useUppercase = false
    private var useLowercase = false
    private var useNumber = false
    private var useSpecialCharacter = false
    private lateinit var binding: PasswordGeneratorViewBinding

    private var minimumNumber = 1
    private var minimumSpecialCharacters = 1
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        binding = PasswordGeneratorViewBinding.inflate(getLayoutInflater())
        viewLayout = binding.root
        sharedPreferences = getContext()
            .getSharedPreferences(
                "keyboardly_devscion_password_generator",
                MODE_PRIVATE
            )
        initClick()
        initHeader()
        setDefaults()
    }

    private fun setDefaults() {

        sharedPreferences.getBoolean("firstTime", true).let {
            if (it) {
                with(sharedPreferences.edit()) {
                    putBoolean("useUppercase", defaultUseUppercase)
                    putBoolean("useLowercase", defaultUseLowercase)
                    putBoolean("useNumber", defaultUseNumber)
                    putBoolean("useSpecialCharacter", defaultUseSpecialCharacter)
                    putInt("minimumNumber", defaultMinimumNumber)
                    putInt("minimumSpecialCharacters", defaultMinimumSpecialCharacters)
                    putInt("passwordCount", defaultPasswordCount)
                    putBoolean("firstTime", false)
                    apply()
                }
            }
        }
        useUppercase = sharedPreferences.getBoolean("useUppercase", defaultUseUppercase)
        useLowercase = sharedPreferences.getBoolean("useLowercase", defaultUseLowercase)
        useNumber = sharedPreferences.getBoolean("useNumber", defaultUseNumber)
        useSpecialCharacter =
            sharedPreferences.getBoolean("useSpecialCharacter", defaultUseSpecialCharacter)
        minimumNumber = sharedPreferences.getInt("minimumNumber", defaultMinimumNumber)
        minimumSpecialCharacters = sharedPreferences.getInt(
            "minimumSpecialCharacters",
            defaultMinimumSpecialCharacters
        )

        //setting values to views
        binding.pwdLengthSlider.progress =
            sharedPreferences.getInt("passwordCount", defaultPasswordCount)
        binding.uppercaseCb.isChecked = useUppercase
        binding.lowercaseCb.isChecked = useLowercase
        binding.numbersCb.isChecked = useNumber
        binding.specialChars.isChecked = useSpecialCharacter
        binding.minNumbersTv.text = minimumNumber.toString()
        binding.minSpecialCharsTv.text = minimumSpecialCharacters.toString()
        generatePassword()
    }

    private fun initClick() {
        binding.apply {

            regenerateBtn.setOnClickListener {
                generatePassword()
            }

            copyBtn.setOnClickListener {
                copyTextToClipboard(
                    binding.password.text?.toString() ?: return@setOnClickListener
                )
            }
            //minimum numbers
            incrementButton.setOnClickListener {
                if (minimumNumber == pwdLengthSlider.progress) return@setOnClickListener
                minimumNumber++
                minNumbersTv.text = minimumNumber.toString()
                generatePassword()
            }
            decrementButton.setOnClickListener {
                if (minimumNumber == 1) return@setOnClickListener
                minimumNumber--
                minNumbersTv.text = minimumNumber.toString()
                generatePassword()
            }

            //maximum numbers
            specialCharsIncButton.setOnClickListener {
                if (minimumSpecialCharacters == pwdLengthSlider.progress) return@setOnClickListener
                minimumSpecialCharacters++
                minSpecialCharsTv.text = minimumSpecialCharacters.toString()
                generatePassword()
            }
            specialCharsDecButton.setOnClickListener {
                if (minimumSpecialCharacters == 1) return@setOnClickListener
                minimumSpecialCharacters--
                minSpecialCharsTv.text = minimumSpecialCharacters.toString()
                generatePassword()
            }
            password.setOnClickListener {
                dependency.requestInput(password, this@PasswordGeneratorView)
            }
            uppercaseCb.setOnCheckedChangeListener { _, isChecked ->
                useUppercase = isChecked
                generatePassword()
            }
            lowercaseCb.setOnCheckedChangeListener { _, isChecked ->
                useLowercase = isChecked
                generatePassword()
            }
            specialChars.setOnCheckedChangeListener { _, isChecked ->
                useSpecialCharacter = isChecked
                generatePassword()
            }
            numbersCb.setOnCheckedChangeListener { _, isChecked ->
                useNumber = isChecked
                generatePassword()
            }

            binding.pwdLengthTv.text = pwdLengthSlider.progress.toString()
            pwdLengthSlider.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    binding.pwdLengthTv.text = pwdLengthSlider.progress.toString()
                    generatePassword()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }

            })

            footerFormReset.setOnClickListener {
                sharedPreferences.edit()
                    .putBoolean("firstTime",true)
                    .apply()
                setDefaults()
            }
        }
    }

    private fun initHeader() {
        binding.apply {
            footerBackBtn.setOnClickListener {
                dependency.viewAddOnNavigation()
            }
        }
    }


    private fun copyTextToClipboard(text: String) {
        val clipboard =
            getContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Label", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(
            getContext(),
            "Text copied to clipboard", Toast.LENGTH_SHORT
        ).show()
    }

    private fun generatePassword() {
        val uppercaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val lowercaseChars = "abcdefghijklmnopqrstuvwxyz"
        val numberChars = "0123456789"
        val specialChars = "!@#$%^&*()-_=+<>,.?/[]{}|"

        var passwordChars = ""

        if (useUppercase) passwordChars += uppercaseChars
        if (useLowercase) passwordChars += lowercaseChars
        if (useNumber) passwordChars += numberChars
        if (useSpecialCharacter) passwordChars += specialChars

        if (passwordChars.isEmpty()) {
            binding.password.setText("")
            return
        }
        with(sharedPreferences.edit()) {
            putBoolean("useUppercase", useUppercase)
            putBoolean("useLowercase", useLowercase)
            putBoolean("useNumber", useNumber)
            putBoolean("useSpecialCharacter", useSpecialCharacter)
            putInt("minimumNumber", minimumNumber)
            putInt("minimumSpecialCharacters", minimumSpecialCharacters)
            putInt("passwordCount", binding.pwdLengthSlider.progress)
            apply()
        }

        val random = SecureRandom()
        val password = StringBuilder()

        // Ensure minimum requirements are met
        if (useNumber) {
            for (i in 1..minimumNumber) {
                password.append(numberChars[random.nextInt(numberChars.length)])
            }
        }

        if (useSpecialCharacter) {
            for (i in 1..minimumSpecialCharacters) {
                password.append(specialChars[random.nextInt(specialChars.length)])
            }
        }

        // Fill up the rest of the password length
        for (i in (password.length + 1)..binding.pwdLengthSlider.progress) {
            password.append(passwordChars[random.nextInt(passwordChars.length)])
        }

        // Shuffling the characters
        val shuffledPassword = password.toString().toMutableList()
        shuffledPassword.shuffle()

        binding.password.setText(shuffledPassword.joinToString(""))
    }


    override fun onDone(text: String, editText: EditText?) {
        Log.d("discount", "text onDone=$text")
        editText?.setText(text)
        dependency.viewLayoutAction()
    }


}