package com.kurante.projectvoice_gdx.game.particles

import com.badlogic.gdx.graphics.g2d.Batch
import com.kurante.projectvoice_gdx.ProjectVoice.Companion.particlePool

class ParticleManager {
    val particles = mutableListOf<CollectionParticle>()

    fun act(delta: Float) {
        for (particle in particles)
            particle.act(delta)
        // Remove particles that have ended their animation
        val size = particles.size - 1
        for (i in size downTo 0) {
            val particle = particles[i]
            if (particle.progress >= particle.duration)
                particlePool.free(particles.removeAt(i))
        }
    }

    fun render(batch: Batch) {
        for (particle in particles)
            particle.render(batch)
    }

    fun clear() {
        for (particle in particles)
            particlePool.free(particle)
        particles.clear()
    }

    fun hasVisibleParticles(): Boolean = particles.isNotEmpty()
}