import re

with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'r') as f:
    content = f.read()

# Find the start of LazyColumn
lazy_col_start = content.find("LazyColumn(\n            modifier = Modifier\n                .fillMaxSize()\n                .padding(innerPadding)")
if lazy_col_start == -1:
    print("Could not find LazyColumn start")
    exit(1)

# Find the end of Scaffold (which is just before `// Interactive Quick Add Expense/Income dialog screen`)
end_marker = "    // Interactive Quick Add Expense/Income dialog screen"
scaffold_end = content.find(end_marker)
if scaffold_end == -1:
    print("Could not find end marker")
    exit(1)

new_content = content[:lazy_col_start] + """Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // BENTO: Welcome & Profile Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Good Morning,",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = (-1).sp
                    )
                }
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        .clickable { viewModel.triggerAutoSync() },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSyncing) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = userName.take(1).uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // BENTO: Main Balance Card (Glass/Mesh style)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.6f)
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.linearGradient(
                            colors = if (themeDark) listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                                     else listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0))
                        )
                    )
                    .border(
                        1.dp,
                        if (themeDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f),
                        RoundedCornerShape(32.dp)
                    )
            ) {
                // Decorative mesh/blur shapes could go here
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = "Total Balance",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (themeDark) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isBalanceHidden) "Rp ••••••••" else rubelFormat.format(totalBalance),
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Black,
                                    color = if (themeDark) Color.White else Color.Black,
                                    letterSpacing = (-1).sp
                                )
                            }
                            IconButton(
                                onClick = { isBalanceHidden = !isBalanceHidden },
                                modifier = Modifier
                                    .background(
                                        if (themeDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f),
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = if (isBalanceHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle",
                                    tint = if (themeDark) Color.White else Color.Black
                                )
                            }
                        }

                        // Bottom Actions inside card
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { showQuickAddDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(54.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (themeDark) Color.White else Color.Black,
                                    contentColor = if (themeDark) Color.Black else Color.White
                                )
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add", fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { onNavigateToTransactions() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(54.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (themeDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f),
                                    contentColor = if (themeDark) Color.White else Color.Black
                                ),
                                elevation = null
                            ) {
                                Icon(Icons.Default.ListAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("History", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // BENTO: 2 Column Grid (Income / Expense)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Income Bento Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFD1FAE5), Color(0xFFA7F3D0))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFF059669).copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = Color(0xFF059669))
                        }
                        Column {
                            Text(
                                text = "Income",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color(0xFF065F46).copy(alpha = 0.8f)
                            )
                            Text(
                                text = if (isBalanceHidden) "•••" else rubelFormat.format(totalIncome),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF064E3B),
                                fontSize = 18.sp,
                                maxLines = 1
                            )
                        }
                    }
                }

                // Expense Bento Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFFFE4E6), Color(0xFFFECDD3))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFE11D48).copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Color(0xFFE11D48))
                        }
                        Column {
                            Text(
                                text = "Expense",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color(0xFF9F1239).copy(alpha = 0.8f)
                            )
                            Text(
                                text = if (isBalanceHidden) "•••" else rubelFormat.format(totalExpense),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF881337),
                                fontSize = 18.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // BENTO: Analytics & AI Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.linearGradient(
                            colors = if (themeDark) listOf(Color(0xFF4C1D95), Color(0xFF312E81))
                                     else listOf(Color(0xFFEDE9FE), Color(0xFFDDD6FE))
                        )
                    )
                    .clickable { showMlReportDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = if (themeDark) Color(0xFFC4B5FD) else Color(0xFF7C3AED),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AI Insights",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (themeDark) Color(0xFFC4B5FD) else Color(0xFF7C3AED),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap to view smart financial analysis",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (themeDark) Color.White else Color(0xFF4C1D95)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(if (themeDark) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Analytics,
                            contentDescription = null,
                            tint = if (themeDark) Color.White else Color(0xFF4C1D95)
                        )
                    }
                }
            }
            
            // BENTO: Recent Transactions
            Text(
                text = "Recent Activity",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )

            if (recentTxs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No transactions yet",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    recentTxs.forEach { tx ->
                        // BENTO: Compact Transaction Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(
                                        if (tx.type == "INCOME") Color(0xFF10B981).copy(alpha = 0.1f)
                                        else Color(0xFFEF4444).copy(alpha = 0.1f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (tx.type == "INCOME") Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = if (tx.type == "INCOME") Color(0xFF10B981) else Color(0xFFEF4444),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tx.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                                Text(
                                    text = tx.category,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = (if (tx.type == "INCOME") "+" else "-") + rubelFormat.format(tx.amount),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Black,
                                color = if (tx.type == "INCOME") Color(0xFF10B981) else Color(0xFFEF4444)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
""" + content[scaffold_end:]

with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'w') as f:
    f.write(new_content)

print("Rewritten successfully.")
