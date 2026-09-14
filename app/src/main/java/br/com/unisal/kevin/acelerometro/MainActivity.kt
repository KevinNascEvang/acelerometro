package br.com.unisal.kevin.acelerometro

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.unisal.kevin.acelerometro.ui.theme.AcelerometroTheme
import br.com.unisal.kevin.acelerometro.ui.theme.EixoX
import br.com.unisal.kevin.acelerometro.ui.theme.EixoY
import br.com.unisal.kevin.acelerometro.ui.theme.EixoZ
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : ComponentActivity(), SensorEventListener {
    var sensorManager: SensorManager? = null
    var sensor: Sensor? = null

    var acelGravidade: Float = 0f
    var acelLinear: Float = 0f

    val gravidade = FloatArray(3)
    val aceleracaoLinear = FloatArray(3)

    var visorEixoX by mutableStateOf("0.0 ms/s2")
    var visorEixoY by mutableStateOf("0.0 ms/s2")
    var visorEixoZ by mutableStateOf("0.0 ms/s2")

    var visorAcelGravidade by mutableStateOf("0.00 ms/s2")
    var visorAcelLinear by mutableStateOf("0.00 ms/s2")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        setContent {
            AcelerometroTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    createInterface()
                }
            }
        }
    }

    @Composable
    fun createInterface() {
        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "Acelerômetro", fontSize = 44.sp, color = MaterialTheme.colorScheme.primary)
                Text(text = "Leitura em tempo real", fontSize = 24.sp, color = MaterialTheme.colorScheme.secondary)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Eixos do Acelerômetro", fontSize = 32.sp, color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 56.dp, end = 56.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "X", fontSize = 30.sp, color = EixoX)
                    Text(text = "Y", fontSize = 30.sp, color = EixoY)
                    Text(text = "Z", fontSize = 30.sp, color = EixoZ)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = visorEixoX, fontSize = 24.sp, color = EixoX)
                    Text(text = visorEixoY, fontSize = 24.sp, color = EixoY)
                    Text(text = visorEixoZ, fontSize = 24.sp, color = EixoZ)
                }
            }

            Column() {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Aceleração da Gravidade", fontSize = 30.sp, color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = visorAcelGravidade, fontSize = 24.sp, color = MaterialTheme.colorScheme.tertiary)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Aceleração Linear", fontSize = 30.sp, color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = visorAcelLinear, fontSize = 24.sp, color = MaterialTheme.colorScheme.tertiary)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onSensorChanged(evento: SensorEvent?) {
        if (evento?.sensor?.type != Sensor.TYPE_ACCELEROMETER)
            return

        val x = evento.values[0]
        val y = evento.values[1]
        val z = evento.values[2]

        val alpha: Float = 0.8f

        gravidade[0] = alpha * gravidade[0] + (1 - alpha) * x
        gravidade[1] = alpha * gravidade[1] + (1 - alpha) * y
        gravidade[2] = alpha * gravidade[2] + (1 - alpha) * z

        aceleracaoLinear[0] = x - gravidade[0]
        aceleracaoLinear[1] = y - gravidade[1]
        aceleracaoLinear[2] = z - gravidade[2]

        acelGravidade = sqrt(
            gravidade[0].pow(2) + gravidade[1].pow(2) + gravidade[2].pow(2)
        )
        acelLinear = sqrt(
            aceleracaoLinear[0].pow(2) + aceleracaoLinear[1].pow(2) + aceleracaoLinear[2].pow(2)
        )

        visorEixoX = "%.1f m/s²".format(gravidade[0])
        visorEixoY = "%.1f m/s²".format(gravidade[1])
        visorEixoZ = "%.1f m/s²".format(gravidade[2])

        visorAcelGravidade = "%.2f m/s²".format(acelGravidade)
        visorAcelLinear = "%.2f m/s²".format(acelLinear)
    }

}
