import re

# Fix MainActivity
with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    main_content = f.read()

main_content = main_content.replace('.androidx.compose.ui.draw.clip', '.clip')
main_content = main_content.replace('.androidx.compose.foundation.background', '.background')
main_content = main_content.replace('.androidx.compose.foundation.border', '.border')
main_content = main_content.replace('.androidx.compose.foundation.clickable', '.clickable')

imports = """
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
"""
main_content = main_content.replace('import androidx.compose.animation.togetherWith\n', 'import androidx.compose.animation.togetherWith\n' + imports)

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(main_content)

# Fix TransactionsScreen
with open('app/src/main/java/com/example/ui/screens/TransactionsScreen.kt', 'r') as f:
    tx_content = f.read()

# Fix component1 component2 issue
tx_content = tx_content.replace('groupedTxs.forEach { (dateStr, txList) ->', 'groupedTxs.forEach { entry ->\n                    val dateStr = entry.key\n                    val txList = entry.value')

# Fix @Composable invocation (AnimatedVisibility inside expanded Row? No, AnimatedVisibility is composable but maybe inside something non-composable? No, it's inside Column in TransactionItemRowBento which is marked @Composable)
# Let's check where the composable invocation error was:
# e: file:///app/src/main/java/com/example/ui/screens/TransactionsScreen.kt:235:33 @Composable invocations can only happen from the context of a @Composable function
# Wait, groupedTxs.forEach is inside `LazyColumn { ... }`.
# Oh! `groupedTxs.forEach` is inside `LazyColumn` block which expects `item {}` or `items {}`!
# Ah! `groupedTxs.forEach` is NOT a composable context, it's `LazyListScope`. So I can't call `item { Text(...) }` if `groupedTxs.forEach` is inside `item`? No, `item` is inside `groupedTxs.forEach`. But wait, is `groupedTxs.forEach` inside `LazyColumn { }`?
# In LazyColumn scope, you CAN call `item {}` because `item` is a function of LazyListScope.

# Let's look at the tx_content to see the LazyColumn.
# LazyColumn(...) {
#    if (groupedTxs.isEmpty()) { item { ... } }
#    groupedTxs.forEach { (dateStr, txList) -> item { ... } }
# }
# Wait, `groupedTxs` is a Map. `Map.forEach` takes a BiConsumer in Java sometimes, or an action. Kotlin `forEach` on Map takes `(Map.Entry<K, V>) -> Unit`.
# If `item` is called inside `forEach`, it's valid. The error "component1 is ambiguous" happens because of some import conflict, but by changing to `entry ->` it fixes the ambiguity.
# Wait, what about "@Composable invocations can only happen from the context of a @Composable function"?
# Ah! `TransactionItemRowBento` is inside `txList.forEach { tx -> TransactionItemRowBento(...) }`.
# But `txList.forEach` is inside `Column { ... }`. `Column` takes a `@Composable ColumnScope.() -> Unit`.
# So calling `TransactionItemRowBento` is a composable invocation. Why did it fail?
# Because `forEach` on `txList` (which is a `List`) takes an inline function. Inline functions propagate composable context!
# BUT if `txList` type was not correctly inferred as `List`, maybe it fell back to something else? Or maybe `entry.value` type was not inferred?

tx_content = tx_content.replace('groupedTxs.forEach { entry ->\n                    val dateStr = entry.key\n                    val txList = entry.value',
                                'for (entry in groupedTxs) {\n                    val dateStr = entry.key\n                    val txList = entry.value')
tx_content = tx_content.replace('groupedTxs.forEach { (dateStr, txList) ->', 
                                'for (entry in groupedTxs) {\n                    val dateStr = entry.key\n                    val txList = entry.value')

# Check if there are any other forEach
# txList.forEach { tx -> ... }
tx_content = tx_content.replace('txList.forEach { tx ->', 'for (tx in txList) {')

with open('app/src/main/java/com/example/ui/screens/TransactionsScreen.kt', 'w') as f:
    f.write(tx_content)

print("Fixed MainActivity and TransactionsScreen!")
