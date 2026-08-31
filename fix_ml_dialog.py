with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'r') as f:
    content = f.read()

target = """        Dialog(
            onDismissRequest = { showMlReportDialog = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .fillMaxHeight(0.88f)
                    .testTag("ml_report_dialog_container"),
                shape = RoundedCornerShape(26.dp),
                color = if (themeDark) Color(0xFF110D1C) else Color(0xFFFAFAFE),
                border = BorderStroke(
                    width = 1.6.dp,
                    color = if (themeDark) Color(0xFF4C229E) else Color(0xFFDCD3FD)
                ),
                tonalElevation = 10.dp
            ) {"""

replacement = """        Dialog(
            onDismissRequest = { showMlReportDialog = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .fillMaxHeight(0.88f)
                    .testTag("ml_report_dialog_container"),
                shape = RoundedCornerShape(32.dp),
                color = if (themeDark) Color(0xFF110D1C) else Color.White,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (themeDark) Color(0xFF4C229E) else Color(0xFFE2E8F0)
                ),
                tonalElevation = 10.dp
            ) {"""

if target in content:
    content = content.replace(target, replacement)
    with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'w') as f:
        f.write(content)
    print("Updated ML Dialog container")
else:
    print("Target ML Dialog not found")
