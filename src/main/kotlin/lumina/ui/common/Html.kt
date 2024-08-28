package gecw.ace.lumina.ui.common

import gecw.ace.lumina.ui.Component

open class Html : Component("html") {
    init {
        add(Component("head").apply {
            add(Component("title").apply {
                add(Component("Lumina", valueVar = true))
            })
            javaClass.getResource("/tailwind.js")?.let {
                println("Found tailwind.js")
                add(
                    """
                    <script>${it.readText()}</script>
                """.trimIndent()
                )
            }
            add(
                Component(
                    """
                <script>
                    function sendMessageToJava(message) {
                        ipc.receiveMessage(JSON.stringify(message));
                    }
                    
                    function clickHandler(id) {
                        sendMessageToJava({id, path: "click"});
                    }

                    function formSubmitHandler(id) {
                        sendMessageToJava({id, path: "formSubmit"});
                    }

                    function mouseEnterHandler(id) {
                        sendMessageToJava({id, path: "mouseEnter"});
                    }

                    function mouseLeaveHandler(id) {
                        sendMessageToJava({id, path: "mouseLeave"});
                    }

                    function changeHandler(id) {
                        sendMessageToJava({id, path: "change"});
                    }

                    function inputHandler(id) {
                        sendMessageToJava({id, path: "input"});
                    }

                    function scrollHandler(id) {
                        sendMessageToJava({id, path: "scroll"});
                    }

                    function keyPressHandler(id) {
                        sendMessageToJava({id, path: "keyPress"});
                    }

                    function keyUpHandler(id) {
                        sendMessageToJava({id, path: "keyUp"});
                    }

                    function keyDownHandler(id) {
                        sendMessageToJava({id, path: "keyDown"});
                    }

                    function contextMenuHandler(id) {
                        sendMessageToJava({id, path: "contextMenu"});
                    }

                    function doubleClickHandler(id) {
                        sendMessageToJava({id, path: "doubleClick"});
                    }

                    function dragHandler(id) {
                        sendMessageToJava({id, path: "drag"});
                    }

                    function dragEndHandler(id) {
                        sendMessageToJava({id, path: "dragEnd"});
                    }

                    function dragEnterHandler(id) {
                        sendMessageToJava({id, path: "dragEnter"});
                    }

                    function dragExitHandler(id) {
                        sendMessageToJava({id, path: "dragExit"});
                    }

                    function dragLeaveHandler(id) {
                        sendMessageToJava({id, path: "dragLeave"});
                    }

                    function dragOverHandler(id) {
                        sendMessageToJava({id, path: "dragOver"});
                    }

                    function dragStartHandler(id) {
                        sendMessageToJava({id, path: "dragStart"});
                    }

                    function dropHandler(id) {
                        sendMessageToJava({id, path: "drop"});
                    }

                    function focusHandler(id) {
                        sendMessageToJava({id, path: "focus"});
                    }

                    function blurHandler(id) {
                        sendMessageToJava({id, path: "blur"});
                    }
                </script> 
                <script>
                    function preventPropagation(e) {
                        e.stopPropagation();
                    }
                </script>
            """.trimIndent(), valueVar = true
                )
            )
        })

        add(
            """
            <script>
            tailwind.config = {
                theme: {
                    extend: {
                        animation: {
                            'expand-top-bottom': 'expand-top-bottom 0.5s',
                        },
                        keyframes: {
                            'expand-top-bottom': {
                                0%:{
                                    scaleY:0;
                                },
                                100%:{
                                    scaleY:1;
                                }
                            },
                        },
                    }
                }
            }
             </script>
        """.trimIndent()
        )
    }
}
