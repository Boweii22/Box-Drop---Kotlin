package com.smartherd.boxdrop

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.BodyType
import org.jbox2d.dynamics.FixtureDef
import org.jbox2d.dynamics.World
import kotlin.math.abs
import kotlin.random.Random



class BoxDropView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val boxes = mutableListOf<Body>()
    private val paint = Paint()
    private val world: World
    private val boxSize = 100f
    private val timeStep = 1.0f / 60.0f  // 60 frames per second
    private val velocityIterations = 6
    private val positionIterations = 2
    private var groundBody: Body? = null
    private var groundRemoved = false // Tracks whether the ground has been removed
    private var removeThreshold = height * 0.2f // The height threshold to remove ground
    private var restoreThreshold = height * 0.8f // The height threshold to restore ground


    init {
        // Initialize Box2D world with gravity
        world = World(Vec2(0f, 10f))  // Gravity pulling down
        paint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Step the physics world
        world.step(timeStep, velocityIterations, positionIterations)

        // Track the highest box
        var highestBoxY = height.toFloat()

        // Draw all boxes
        for (box in boxes) {
            val position = box.position
            paint.color = box.userData as Int
            canvas.drawRect(
                position.x * boxSize - boxSize / 2,
                position.y * boxSize - boxSize / 2,
                position.x * boxSize + boxSize / 2,
                position.y * boxSize + boxSize / 2,
                paint
            )
            // Track the highest Y position
            highestBoxY = minOf(highestBoxY, position.y * boxSize)
        }

        // Check if we need to remove or restore the ground
        if (highestBoxY <= removeThreshold && !groundRemoved) {
            removeGround() // Open the ground
        } else if (highestBoxY >= restoreThreshold && groundRemoved) {
            restoreGround() // Close the ground
        }

        // Redraw at 60 FPS
        invalidate()
    }

    // Method to remove the ground
    private fun removeGround() {
        if (groundBody != null) {
            world.destroyBody(groundBody) // Remove the ground
            groundBody = null
            groundRemoved = true // Mark ground as removed
        }
    }

    // Method to restore the ground
    private fun restoreGround() {
        if (groundBody == null) {
            createGround(width, height) // Recreate the ground
            groundRemoved = false // Mark ground as restored
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            // Add a new box at the touch location
            val newBox = createBox(event.x / boxSize, 0f)  // Add box at the top of the screen
            boxes.add(newBox)
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun createBox(x: Float, y: Float): Body {
        val bodyDef = BodyDef()
        bodyDef.type = BodyType.DYNAMIC
        bodyDef.position.set(x, y)
        val body = world.createBody(bodyDef)

        val shape = PolygonShape()
        shape.setAsBox(0.5f, 0.5f)  // Box size

        val fixtureDef = FixtureDef()
        fixtureDef.shape = shape
        fixtureDef.density = 1.0f
        fixtureDef.friction = 0.3f
        body.createFixture(fixtureDef)

        // Assign a random color to the box
        body.userData = randomColor()

        return body
    }

    private fun randomColor(): Int {
        return Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createGround(w, h)
    }

//    private fun createGround(width: Int, height: Int) {
//        if (groundBody != null) {
//            world.destroyBody(groundBody) // Destroy previous ground, if any
//        }
//
//        val groundBodyDef = BodyDef()
//        // Position the ground at the bottom of the screen
//        groundBodyDef.position.set(width / 2f / boxSize, height / boxSize)
//        groundBody = world.createBody(groundBodyDef)
//
//        val groundShape = PolygonShape()
//        // Set the ground to be as wide as the screen
//        groundShape.setAsBox(width.toFloat() / boxSize, 0.5f)
//
//        groundBody!!.createFixture(groundShape, 0.0f)  // Static body, so no density
//    }
private fun createGround(width: Int, height: Int) {
    if (groundBody != null) {
        world.destroyBody(groundBody) // Destroy previous ground, if any
    }

    // Create ground at the bottom of the screen
    val groundBodyDef = BodyDef()
    groundBodyDef.position.set(width / 2f / boxSize, height / boxSize)
    groundBody = world.createBody(groundBodyDef)

    val groundShape = PolygonShape()
    groundShape.setAsBox(width.toFloat() / boxSize, 0.5f) // Wide ground

    groundBody!!.createFixture(groundShape, 0.0f)  // Static body for the ground

    // Create left boundary
    val leftWallBodyDef = BodyDef()
    leftWallBodyDef.position.set(0f, height / 2f / boxSize)  // Left side at x = 0
    val leftWallBody = world.createBody(leftWallBodyDef)

    val leftWallShape = PolygonShape()
    leftWallShape.setAsBox(0.5f, height.toFloat() / boxSize) // Tall wall on the left

    leftWallBody.createFixture(leftWallShape, 0.0f)  // Static body for left wall

    // Create right boundary
    val rightWallBodyDef = BodyDef()
    rightWallBodyDef.position.set(width.toFloat() / boxSize, height / 2f / boxSize)  // Right side at x = width
    val rightWallBody = world.createBody(rightWallBodyDef)

    val rightWallShape = PolygonShape()
    rightWallShape.setAsBox(0.5f, height.toFloat() / boxSize) // Tall wall on the right

    rightWallBody.createFixture(rightWallShape, 0.0f)  // Static body for right wall
}

}

