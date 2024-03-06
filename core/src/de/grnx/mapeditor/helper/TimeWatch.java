package de.grnx.mapeditor.helper;

import com.badlogic.gdx.Gdx;

/**
 * TimeWatch for counting the time a specific event happens relative to some other periodic event.
 * The {@link #render() render} method needs to be called in the main rendering method. The amount of ticks passed in there
 * will be compared to the amount of ticks counted using {@link #canExecute() canExecute} every time the latter is called.
 * It increments one tick and returns true if the initial offset was overcome AND a period has passed.
 * If the {@link #heldTicks heldTicks} incremented in the {@link #canExecute() canExecute} falls behind, the timer is reset,
 * and the offset needs to be elapsed again.
 * 
 * @param initialDelaySeconds The initial buffer of seconds that needs to be overcome for the periodic triggering to start occurring.
 * @param periodicOffsetSeconds The periodic ticks needed to be passed for another trigger to happen.
 */
public class TimeWatch {
	private boolean accelerate;
    private float delaySeconds;
    private int delayTicks;

    private float periodicSeconds;
    private int periodicTicks;

    private int renderedTicks;
    private int heldTicks;
    private int periodicTicksPassed;

    private boolean running;
    private boolean initialDelayPassed = false;

    /**
     * Constructs a TimeWatch with the specified initial delay and periodic offset.
     * 
     * @param initialDelaySeconds The initial buffer of seconds that needs to be overcome for the periodic triggering to start occurring.
     * @param periodicOffsetSeconds The periodic ticks needed to be passed for another trigger to happen.
     * 
     * The trigger rate will stay constant and not increase the longer the conditions are meet.
     * @see #TimeWatch(float, float, boolean)
     */
    public TimeWatch(float initialDelaySeconds, float periodicOffsetSeconds) {
        this.setDelaySeconds(initialDelaySeconds);
        this.setPeriodicSeconds(periodicOffsetSeconds);
        this.accelerate=false;
    }
    
    /**
     * Constructs a TimeWatch with the specified initial delay, periodic offset and acceleration configuration.
     * If true, for every {@link #initialDelayPassed} the trigger rate will increase; Default is false
     * 
     * @param initialDelaySeconds The initial buffer of seconds that needs to be overcome for the periodic triggering to start occurring.
     * @param periodicOffsetSeconds The periodic ticks needed to be passed for another trigger to happen.
     * @param accelerate The boolean telling wether to speed up when holding or keeping a constant trigger rate. Default, as in the other constructor, is false.
     */
    public TimeWatch(float initialDelaySeconds, float periodicOffsetSeconds, boolean accelerate) {
        this.setDelaySeconds(initialDelaySeconds);
        this.setPeriodicSeconds(periodicOffsetSeconds);
        this.accelerate=accelerate;
    }

    /**
     * Gets the delay in seconds.
     * 
     * @return The delay in seconds.
     */
    public float getDelaySeconds() {
        return delayTicks / Gdx.graphics.getFramesPerSecond();
    }

    /**
     * Sets the delay in seconds.
     * 
     * @param seconds The delay in seconds.
     */
    public void setDelaySeconds(float seconds) {
        this.delaySeconds = seconds;
        setDelayTicks((int) (this.delaySeconds * Gdx.graphics.getFramesPerSecond()));
    }

    /**
     * Gets the delay in ticks.
     * 
     * @return The delay in ticks.
     */
    public int getDelayTicks() {
        return this.delayTicks;
    }

    /**
     * Sets the delay in ticks.
     * 
     * @param ticks The delay in ticks.
     */
    public void setDelayTicks(int ticks) {
        this.delayTicks = ticks;
    }

    /**
     * Gets the periodic interval in seconds.
     * 
     * @return The periodic interval in seconds.
     */
    public float getPeriodicSeconds() {
        return periodicTicks / Gdx.graphics.getFramesPerSecond();
    }

    /**
     * Sets the periodic interval in seconds.
     * 
     * @param seconds The periodic interval in seconds.
     */
    public void setPeriodicSeconds(float seconds) {
        this.periodicSeconds = seconds;
        setPeriodicTicks((int) (this.periodicSeconds * Gdx.graphics.getFramesPerSecond()));
    }

    /**
     * Gets the periodic interval in ticks.
     * 
     * @return The periodic interval in ticks.
     */
    public int getPeriodicTicks() {
        return this.periodicTicks;
    }

    /**
     * Sets the periodic interval in ticks.
     * 
     * @param ticks The periodic interval in ticks.
     */
    public void setPeriodicTicks(int ticks) {
        this.periodicTicks = ticks;
    }

    /**
     * Renders the TimeWatch. Should be called in the main rendering method.
     * 
     * If the timer is running, it updates the ticks and checks for triggering conditions.
     * The timer will start automatically on the first execution of {@link #canExecute() canExecute}
     * If the {@link #heldTicks heldTicks} controlled in the method {@link #canExecute() canExecute} falls behind, the timer is reset,
     * and running is terminated until the execution condition is meet again.
     */
    public void render() {
        if (running) {
            setDelayTicks((int) (this.delaySeconds * Gdx.graphics.getFramesPerSecond()));
            setPeriodicTicks((int) (this.periodicSeconds * Gdx.graphics.getFramesPerSecond()));

            this.renderedTicks++;

            if (heldTicks >= this.renderedTicks && heldTicks != 0) {
                this.initialDelayPassed = false;
                if (heldTicks >= this.delayTicks)
                    this.initialDelayPassed = true;
            } else {
                this.heldTicks = this.renderedTicks = 0;
                running = false;
                this.initialDelayPassed = false;
            }
        }
    }

    private boolean initialDelayDurationOffsetPassed() {
        if (!running)
            running = true;
        this.heldTicks++;
        return initialDelayPassed;
    }

    /**
     * Checks if the TimeWatch can execute a trigger.
     * Should be called in a sub condition of the main rendering method. 
     * @return True if the initial delay has passed, and the periodic interval has been reached; otherwise, false.
     */
    public boolean canExecute() {
        if (initialDelayDurationOffsetPassed()) {
            if (++periodicTicksPassed+(periodicTicksPassed*(periodicTicksPassed/delayTicks)*( accelerate? 1 : 0)/2) >= periodicTicks) {
//            	if (++periodicTicksPassed >= periodicTicks) {
                periodicTicksPassed = 0;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
