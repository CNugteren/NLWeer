package foss.cnugteren.nlweer.ui.knmi_rain_m1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class KnmiRainM1ViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "[KNMI] Rain last hour"
    }
    val text: LiveData<String> = _text
}