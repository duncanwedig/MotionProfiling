import java.util.ArrayList;

//This exists just in case I actually ever decide to do anything with this
enum MotionProfileType {
	POSITION_BASED, TIMEY_TRAPEZOID, S_CURVE;
}

public class MotionProfiler {
	
	private MotionProfileType m_profileType;
	//This exists, but is basically just a boolean until I actually implement an S-curve
	private int m_profileTraits;
	
	private double m_accelTime, m_cruiseTime;
	private double m_accelDistance, m_cruiseDistance;
	private double m_cruiseVelocity, m_maxAcceleration;
	private int m_setpoint;
	private int m_state;
	
	private ArrayList<Integer> m_positions = new ArrayList<Integer>();
	private ArrayList<Double> m_velocities = new ArrayList<Double>();
	
	public MotionProfiler(int setpoint, double cruiseVelocity, double maxAcceleration, MotionProfileType profileType) {
		
		this.m_profileType = profileType;
		
		this.m_cruiseVelocity = cruiseVelocity;
		this.m_setpoint = setpoint;
		this.m_maxAcceleration = maxAcceleration;
		
		this.m_accelTime = this.m_cruiseVelocity / this.m_maxAcceleration;
		this.m_accelDistance = 0.5 * this.m_maxAcceleration * Math.pow(this.m_accelTime, 2);
		//This is basically just triangle detection, which happens to be fairly simple with the system used
		if (this.m_accelDistance * 2 > this.m_setpoint) {
			this.m_accelDistance = this.m_setpoint / 2;
		}
		this.m_cruiseDistance = this.m_setpoint - 2 * this.m_accelDistance;
		this.m_cruiseTime = this.m_cruiseVelocity / this.m_cruiseDistance;
		
		// 0 means accelerating, 1 means cruising, 2 means decelerating
		this.m_state = 0;
	}
	
	public void generatePoints() {
		int currentPosition = 0;
		double currentVelocity = 0;
		for (int i = 1; i <= this.m_setpoint; i++) {

			m_positions.add(currentPosition);
			m_velocities.add(currentVelocity);
			System.out.println(String.valueOf(currentPosition) + "," + String.valueOf(currentVelocity));
			currentPosition = i;
			
			if (currentPosition >= this.m_accelDistance && currentPosition < this.m_accelDistance + this.m_cruiseDistance) {
				this.m_state = 1;
			} else if (currentPosition >= this.m_accelDistance + this.m_cruiseDistance) {
				this.m_state = 2;
			}
			if (this.m_state == 0) {
				currentVelocity = Math.sqrt(Math.pow(currentVelocity, 2) + 2 * this.m_maxAcceleration);
			} else if (this.m_state == 1) {
				currentVelocity = this.m_cruiseVelocity;
			} else if (this.m_state == 2) {
				currentVelocity = Math.sqrt(Math.pow(currentVelocity, 2) - 2 * this.m_maxAcceleration);
			}
		}
	}
	
	//Convenience functions, for use when you're actually using this class
	public int getPosition(int index) {
		return this.m_positions.get(index);
	}
	
	public double getVelocity(int index) {
		return this.m_velocities.get(index);
	}
	
	public double getVelocityFromPosition(int position) {
		return this.getVelocity(position);
	}
	
}
