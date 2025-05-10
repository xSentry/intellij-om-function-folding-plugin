package com.omnedia.functionfolding

import com.intellij.lang.javascript.psi.JSBlockStatement
import com.intellij.lang.javascript.psi.JSFunction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.FoldingModel
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.util.PsiTreeUtil

class ToggleFunctionFoldsAction : AnAction(), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return

        val functions = PsiTreeUtil.findChildrenOfType(psiFile, JSFunction::class.java)
        val foldingModel: FoldingModel = editor.foldingModel

        ApplicationManager.getApplication().runReadAction {
            foldingModel.runBatchFoldingOperation {
                for (fn in functions) {
                    val body = PsiTreeUtil.findChildOfType(fn, JSBlockStatement::class.java) ?: continue
                    val start = body.textRange.startOffset
                    val end   = body.textRange.endOffset
                    var region = foldingModel.getFoldRegion(start, end)
                    if (region == null) {
                        region = foldingModel.addFoldRegion(start, end, "{…}")
                    }
                    region?.let { it.isExpanded = !it.isExpanded }
                }
            }
        }
    }
}
