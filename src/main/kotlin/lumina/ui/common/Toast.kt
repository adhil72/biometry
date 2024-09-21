package gecw.cse.lumina.ui.common

import gecw.ace.lumina.Lumina
import gecw.ace.lumina.ui.Component
import gecw.ace.lumina.ui.common.Div
import gecw.ace.lumina.ui.common.Span
import gecw.ace.lumina.utils.Resource
import java.util.UUID

enum class ToastType(val className: String) {
    SUCCESS("bg-green-500 text-white"),
    ERROR("bg-red-500 text-white"),
    INFO("bg-blue-500 text-white"),
    DEFAULT("bg-gray-500 text-white")
}

fun toastIcon(type: ToastType):String?{
    return when (type){
        ToastType.SUCCESS -> "<svg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round' class='lucide lucide-badge-check'><path d='M3.85 8.62a4 4 0 0 1 4.78-4.77 4 4 0 0 1 6.74 0 4 4 0 0 1 4.78 4.78 4 4 0 0 1 0 6.74 4 4 0 0 1-4.77 4.78 4 4 0 0 1-6.75 0 4 4 0 0 1-4.78-4.77 4 4 0 0 1 0-6.76Z'/><path d='m9 12 2 2 4-4'/></svg>"
        ToastType.ERROR -> "<svg xmlns='http://www.w3.org/2000/svg' class='h-6 w-6' fill='none' viewBox='0 0 24 24' stroke='currentColor'><path stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M6 18L18 6M6 6l12 12'/></svg>"
        ToastType.INFO -> "<svg xmlns='http://www.w3.org/2000/svg' class='h-6 w-6' fill='none' viewBox='0 0 24 24' stroke='currentColor'><path stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M12 14l9-5-9-5-9 5 9 5z'/></svg>"
        ToastType.DEFAULT -> "<svg xmlns='http://www.w3.org/2000/svg' class='h-6 w-6' fill='none' viewBox='0 0 24 24' stroke='currentColor'><path stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M12 14l9-5-9-5-9 5 9 5z'/></svg>"
    }


}

class ToastLayout : Div() {
    init {
        id = "toast-layout"
        cn(
            "w-full h-screen z-50 flex flex-col items-center max-h-[400px] pt-5 " +
                    "absolute pointer-events-none overflow-y-auto"
        )
        addJavaScript()
    }

    private fun addJavaScript() {
        add(
            """
            <script>
                function createToast(title, toastType, duration, icon) {
                    const toastLayout = document.getElementById("toast-layout");
                    
                    // Create toast container
                    const toastItem = document.createElement('div');
                    toastItem.className = `font-semibold p-2 border rounded-lg shadow-lg flex items-center animate-expand-top-bottom text-lg w-[350px] h-fit `+toastType;
                    
                    // Create icon
                    if (icon) {
                        const iconSpan = document.createElement('span');
                        iconSpan.innerHTML = icon;
                        toastItem.appendChild(iconSpan);
                    }
                    
                    // Create title span
                    const titleSpan = document.createElement('span');
                    titleSpan.className = 'ml-2';
                    titleSpan.textContent = title;
                    toastItem.appendChild(titleSpan);

                    // Add toast to layout
                    toastLayout.appendChild(toastItem);

                    // Remove toast after duration
                    setTimeout(() => {
                        if (toastItem.parentNode) {
                            toastItem.parentNode.removeChild(toastItem);
                        }
                    }, duration * 1000);
                }
            </script>
            """.trimIndent()
        )
    }
}

fun createToast(title: String, duration: Int = 5, toastType: ToastType = ToastType.DEFAULT) {
    // Ensure that the className is properly escaped
    val toastTypeClassName = toastType.className.replace("'", "\\'")
    Lumina.executeJS(
        """
        createToast('${title.replace("'", "\\'")}', '${toastTypeClassName}', ${duration}, "${toastIcon(toastType)}");
        """.trimIndent()
    )
}
