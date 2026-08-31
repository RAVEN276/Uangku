import re

with open('app/src/main/java/com/example/ui/screens/BudgetScreen.kt', 'r') as f:
    content = f.read()

target = """    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = if (activeTab == 0) "Anggaran & Alarm" else "Target Menabung",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (activeTab == 0) "Atur sasaran pengeluaran bulanan agar terhindar dari pemborosan berlebih." else "Lacak kemajuan menabung Anda untuk membeli barang atau tujuan impian.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val activeBtnColor = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                    val inactiveBtnColor = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = { activeTab = 0 },
                        colors = if (activeTab == 0) activeBtnColor else inactiveBtnColor,
                        shape = RoundedCornerShape(32.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Text("Anggaran", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = { activeTab = 1 },
                        colors = if (activeTab == 1) activeBtnColor else inactiveBtnColor,
                        shape = RoundedCornerShape(32.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Text("Target", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            if (activeTab == 0) {"""

replacement = """    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = if (themeDark) listOf(Color(0xFF0F172A), Color(0xFF020617))
                                 else listOf(Color(0xFFF8FAFC), Color(0xFFE2E8F0))
                    )
                )
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // SPATIAL HEADER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                        .background(
                            if (themeDark) Color(0xFF1E293B).copy(alpha = 0.9f) 
                            else Color.White.copy(alpha = 0.9f)
                        )
                        .border(
                            1.dp,
                            if (themeDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f),
                            RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                        )
                        .padding(top = 48.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = "Anggaran & Target",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = (-1).sp
                        )
                        
                        // Tab Selector Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = if (themeDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.05f),
                                    shape = RoundedCornerShape(32.dp)
                                )
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val activeBtnColor = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                            val inactiveBtnColor = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Button(
                                onClick = { activeTab = 0 },
                                colors = if (activeTab == 0) activeBtnColor else inactiveBtnColor,
                                shape = RoundedCornerShape(32.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 12.dp)
                            ) {
                                Text("Anggaran", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            Button(
                                onClick = { activeTab = 1 },
                                colors = if (activeTab == 1) activeBtnColor else inactiveBtnColor,
                                shape = RoundedCornerShape(32.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 12.dp)
                            ) {
                                Text("Target", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
                
                // Content Below Header
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
                ) {
            if (activeTab == 0) {"""

if target in content:
    content = content.replace(target, replacement)
    
    # Let's fix the closing braces again, since we added Box, Column (2 extra braces)
    old_close = """            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    // Modal dialogue limit set up"""
    
    new_close = """            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
        }
        }
    }

    // Modal dialogue limit set up"""
    
    content = content.replace(old_close, new_close)
    
    # Also fix Scaffold containerColor
    scaffold_old = "    Scaffold(\n        modifier = modifier.fillMaxSize(),"
    scaffold_new = "    Scaffold(\n        modifier = modifier.fillMaxSize(),\n        containerColor = Color.Transparent,"
    content = content.replace(scaffold_old, scaffold_new)
    
    with open('app/src/main/java/com/example/ui/screens/BudgetScreen.kt', 'w') as f:
        f.write(content)
    print("Budget updated successfully!")
else:
    print("Target block not found!")
