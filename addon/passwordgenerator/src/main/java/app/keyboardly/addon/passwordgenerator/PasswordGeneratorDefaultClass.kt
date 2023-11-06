package app.keyboardly.addon.passwordgenerator

import android.view.View
import app.keyboardly.lib.DefaultClass
import app.keyboardly.lib.KeyboardActionDependency
import app.keyboardly.addon.passwordgenerator.views.PasswordGeneratorView

class PasswordGeneratorDefaultClass(
    dependency: KeyboardActionDependency
):DefaultClass(dependency){


    private val passwordGeneratorView = PasswordGeneratorView(dependency)

    override fun getView(): View? {
        return passwordGeneratorView.getView()
    }

    override fun onCreate() {

    }
}