with open('app/src/main/java/com/example/ui/screens/TransactionsScreen.kt', 'r') as f:
    content = f.read()

import re

# Need to add imports for SwipeToDismissBox, rememberSwipeToDismissBoxState, SwipeToDismissBoxValue
imports = """
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.animation.animateColorAsState
"""

if "import androidx.compose.material3.SwipeToDismissBox" not in content:
    content = content.replace("import androidx.compose.material3.*", "import androidx.compose.material3.*\n" + imports)


old_row_pattern = r"@Composable\s+fun TransactionItemRowBento\([\s\S]*?\}\s*\}\s*\}"

new_row = """@Composable
fun TransactionItemRowBento(
    tx: Transaction,
    themeDark: Boolean,
    rubelFormat: NumberFormat,
    onDelete: (Transaction) -> Unit,
    onUpdate: (Transaction) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onUpdate(tx)
                    false // Return false so it bounces back (we handle edit via dialog usually)
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete(tx)
                    true // Return true to dismiss the item
                }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.Settled -> Color.Transparent
                    SwipeToDismissBoxValue.StartToEnd -> Color(0xFF3B82F6) // Blue for edit
                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFEF4444) // Red for delete
                }
            )
            val alignment = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                else -> Alignment.Center
            }
            val icon = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Edit
                SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
                else -> Icons.Default.Delete
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
                    .background(color)
                    .padding(horizontal = 24.dp),
                contentAlignment = alignment
            ) {
                if (direction != SwipeToDismissBoxValue.Settled) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (themeDark) Color(0xFF0F172A) else Color(0xFFF8FAFC))
                    .clickable { expanded = !expanded }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (tx.type == "INCOME") Color(0xFF10B981).copy(alpha = 0.15f)
                                else Color(0xFFEF4444).copy(alpha = 0.15f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (tx.type == "INCOME") Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = if (tx.type == "INCOME") Color(0xFF10B981) else Color(0xFFEF4444),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tx.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (themeDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = tx.category,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Text(
                        text = (if (tx.type == "INCOME") "+" else "-") + rubelFormat.format(tx.amount),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = if (tx.type == "INCOME") Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                }

                // Expanded Actions
                androidx.compose.animation.AnimatedVisibility(visible = expanded) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onUpdate(tx) },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF3B82F6).copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF3B82F6), modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { onDelete(tx) },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFEF4444).copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    )
}"""

content = re.sub(old_row_pattern, new_row, content)

with open('app/src/main/java/com/example/ui/screens/TransactionsScreen.kt', 'w') as f:
    f.write(content)

print("Updated TransactionItemRowBento")
