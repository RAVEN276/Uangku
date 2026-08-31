with open('app/src/main/java/com/example/ui/screens/TransactionsScreen.kt', 'r') as f:
    content = f.read()

old_str = """                    // BENTO: Search and Filter Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search activity...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = if (themeDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f),
                                focusedBorderColor = Color(0xFF10B981)
                            )
                        )
                        
                    }"""

new_str = """                    // BENTO: Search and Filter
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search activity...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = if (themeDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f),
                            focusedBorderColor = Color(0xFF10B981)
                        )
                    )"""

content = content.replace(old_str, new_str)

with open('app/src/main/java/com/example/ui/screens/TransactionsScreen.kt', 'w') as f:
    f.write(content)
print("Search bar cleaned")
