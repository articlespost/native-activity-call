package com.native_call_test.native_call_test

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

class NativeActivity : ComponentActivity() {
    private val viewModel: NativeViewModel by viewModels()

    private lateinit var avatarText: TextView
    private lateinit var nameTextView: TextView
    private lateinit var roleTextView: TextView
    private lateinit var idTextView: TextView
    private lateinit var statusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setBackgroundColor(Color.parseColor("#F5F7FA"))
            setPadding(48, 96, 48, 48)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val avatarContainer = CardView(this).apply {
            radius = 120f
            setCardBackgroundColor(Color.parseColor("#3F51B5"))
            cardElevation = 8f
            layoutParams = LinearLayout.LayoutParams(200, 200).apply {
                bottomMargin = 32
            }
        }

        // TextView das iniciais agora é variável global para ser atualizada
        avatarText = TextView(this).apply {
            text = "..."
            textSize = 32f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        avatarContainer.addView(avatarText)
        rootLayout.addView(avatarContainer)

        val infoCard = CardView(this).apply {
            radius = 24f
            setCardBackgroundColor(Color.WHITE)
            cardElevation = 4f
            useCompatPadding = true
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 48
            }
        }

        val cardContent = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        statusTextView = TextView(this).apply {
            text = "Carregando perfil..."
            textSize = 14f
            setTextColor(Color.parseColor("#9E9E9E"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 16 }
        }

        nameTextView = TextView(this).apply {
            textSize = 22f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.parseColor("#212121"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 8 }
        }

        roleTextView = TextView(this).apply {
            textSize = 16f
            setTextColor(Color.parseColor("#757575"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 24 }
        }

        idTextView = TextView(this).apply {
            textSize = 13f
            setTextColor(Color.parseColor("#BDBDBD"))
            gravity = Gravity.CENTER
        }

        cardContent.addView(statusTextView)
        cardContent.addView(nameTextView)
        cardContent.addView(roleTextView)
        cardContent.addView(idTextView)
        infoCard.addView(cardContent)
        rootLayout.addView(infoCard)

        val btnVoltar = Button(this).apply {
            text = "Voltar para o Flutter"
            isAllCaps = false
            textSize = 16f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#3F51B5"))
            elevation = 4f
            setOnClickListener { finish() }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                140
            )
        }
        rootLayout.addView(btnVoltar)

        setContentView(rootLayout)

        val userId = intent.getIntExtra("USER_ID", -1)
        if (savedInstanceState == null) {
            viewModel.loadUserData(userId)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            statusTextView.text = "Carregando dados..."
                            avatarText.text = "..."
                            nameTextView.text = ""
                            roleTextView.text = ""
                            idTextView.text = ""
                        }
                        is UiState.Success -> {
                            statusTextView.text = "ATIVO"
                            nameTextView.text = state.user.name
                            roleTextView.text = state.user.role
                            idTextView.text = "ID de Registro: #${state.user.id}"
                            
                            // Geração dinâmica das iniciais a partir do nome
                            avatarText.text = getInitials(state.user.name)
                        }
                        is UiState.Error -> {
                            statusTextView.text = "FALHA NA CONEXÃO"
                            avatarText.text = "ERR"
                            nameTextView.text = "Erro ao carregar"
                            roleTextView.text = state.message
                            idTextView.text = ""
                        }
                    }
                }
            }
        }
    }

    // Função utilitária para extrair até 2 iniciais do nome completo
    private fun getInitials(name: String): String {
        val parts = name.trim().split("\\s+".toRegex())
        return if (parts.size >= 2) {
            "${parts.first().take(1)}${parts.last().take(1)}".uppercase()
        } else if (parts.size == 1 && parts.first().isNotEmpty()) {
            parts.first().take(2).uppercase()
        } else {
            "US"
        }
    }
}