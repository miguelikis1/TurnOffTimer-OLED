package com.oled.sleeptimer.receiver

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Receptor de políticas de administración de dispositivo para permitir el apagado / bloqueo
 * automático de pantalla sin requerir permisos de accesibilidad invasivos o root.
 */
class SleepDeviceAdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Log.i(TAG, "Permiso de Administrador de Dispositivo activado.")
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Log.i(TAG, "Permiso de Administrador de Dispositivo desactivado.")
    }

    companion object {
        private const val TAG = "SleepDeviceAdmin"

        fun getComponentName(context: Context): ComponentName {
            return ComponentName(context, SleepDeviceAdminReceiver::class.java)
        }

        fun isAdminActive(context: Context): Boolean {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
                ?: return false
            return dpm.isAdminActive(getComponentName(context))
        }

        /**
         * Crea el Intent del sistema para solicitar al usuario la concesión
         * del permiso de Administrador de Dispositivo.
         */
        fun getAddAdminIntent(context: Context, explanation: String): Intent {
            return Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, getComponentName(context))
                putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, explanation)
            }
        }

        /**
         * Apaga / bloquea la pantalla inmediatamente usando lockNow().
         * @return true si se pudo ejecutar, false si no cuenta con el permiso activo.
         */
        fun lockScreen(context: Context): Boolean {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
                ?: return false
            val component = getComponentName(context)
            return if (dpm.isAdminActive(component)) {
                try {
                    dpm.lockNow()
                    true
                } catch (e: Exception) {
                    Log.e(TAG, "Error ejecutando lockNow()", e)
                    false
                }
            } else {
                Log.w(TAG, "No se puede apagar pantalla: Administrador no concedido.")
                false
            }
        }

        /**
         * Desactiva el permiso de administrador para permitir al usuario desinstalar la app
         * fácilmente en cualquier momento sin navegar por los ajustes profundos del sistema.
         */
        fun removeAdmin(context: Context): Boolean {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
                ?: return false
            val component = getComponentName(context)
            return if (dpm.isAdminActive(component)) {
                try {
                    dpm.removeActiveAdmin(component)
                    true
                } catch (e: Exception) {
                    Log.e(TAG, "Error revocando permiso de administrador", e)
                    false
                }
            } else {
                false
            }
        }
    }
}
