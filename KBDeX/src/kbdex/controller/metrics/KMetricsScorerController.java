/*
 * KMetricsScorerState.java
 * Created on 2011/11/26
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.controller.metrics;

import kbdex.controller.IKDiscourseControllerListener;

/**
 * @author macchan
 * @TODO 懸案事項　途中ステップでACTIVATEした場合，FrameNoと配列番号があわなくなる
 */
public class KMetricsScorerController implements IKDiscourseControllerListener {

	enum State {
		ACTIVE, ACTIVATING, INACTIVE
	};

	private State state = State.INACTIVE;

	private KTemporalMetricsCasher scorer;

	public KMetricsScorerController(KTemporalMetricsCasher scorer) {
		this.scorer = scorer;
	}

	//	/**
	//	 * @return the scorer
	//	 */
	//	public KTemporalMetricsScorer getScorer() {
	//		return scorer;
	//	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return state == State.ACTIVE || state == State.ACTIVATING;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		if (isActive() && !active) {
			scorer.hardReset();//TMP 状態機械を再設計すべし
			state = State.INACTIVE;
		} else if (!isActive() && active) {
			scorer.hardReset();//TMP 状態機械を再設計すべし
			state = State.ACTIVATING;
		}
	}

	/* (non-Javadoc)
	 * @see kbdex.controller.IKDiscourseControllerListener#hardReset()
	 */
	@Override
	public void hardReset() {
		if (state == State.ACTIVATING) {
			state = State.ACTIVE;
		}
		if (state == State.ACTIVE) {
			scorer.hardReset();
		}
	}

	/* (non-Javadoc)
	 * @see kbdex.controller.IKDiscourseControllerListener#reset()
	 */
	@Override
	public void reset() {
		if (state == State.ACTIVATING) {
			state = State.ACTIVE;
		}
		if (state == State.ACTIVE) {
			scorer.reset();
		}
	}

	/* (non-Javadoc)
	 * @see kbdex.controller.IKDiscourseControllerListener#tick()
	 */
	@Override
	public void tick() {
		if (state == State.ACTIVE) {
			scorer.tick();
		}
	}

	/* (non-Javadoc)
	 * @see kbdex.controller.IKDiscourseControllerListener#isAnimationFinished()
	 */
	@Override
	public boolean isAnimationFinished() {
		return true;
	}

	/* (non-Javadoc)
	 * @see kbdex.controller.IKDiscourseControllerListener#refreshWithAnimation()
	 */
	@Override
	public void refreshWithAnimation() {
	}

	/* (non-Javadoc)
	 * @see kbdex.controller.IKDiscourseControllerListener#refreshWithoutAnimation()
	 */
	@Override
	public void refreshWithoutAnimation() {
	}

}
