function animationComplete() {
    console.log("Animation Complete");
    if (typeof Android !== 'undefined' && Android.showToast) {
        Android.showToast("Animation Complete");
    } else {
        console.error("Android.showToast is not defined");
    }
}

gsap.set("#logo", { opacity: 0 });
gsap.to("#logo", { duration: 3, opacity: 1, onComplete: animationComplete });