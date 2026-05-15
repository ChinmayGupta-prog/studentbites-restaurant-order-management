const navToggle = document.querySelector("[data-nav-toggle]");
const navLinks = document.querySelector("[data-nav-links]");

if (navToggle && navLinks) {
    navToggle.addEventListener("click", () => {
        navLinks.classList.toggle("open");
    });
}

const savedTheme = localStorage.getItem("studentbites-theme");
if (savedTheme) {
    document.documentElement.dataset.theme = savedTheme;
}

document.querySelectorAll("[data-theme-toggle]").forEach((button) => {
    button.addEventListener("click", () => {
        const nextTheme = document.documentElement.dataset.theme === "dark" ? "light" : "dark";
        document.documentElement.dataset.theme = nextTheme;
        localStorage.setItem("studentbites-theme", nextTheme);
    });
});

const autoRefreshTarget = document.querySelector("[data-auto-refresh='true']");
if (autoRefreshTarget) {
    setTimeout(() => window.location.reload(), 30000);
}

document.querySelectorAll(".password-field").forEach((field) => {
    const input = field.querySelector("[data-password-input]");
    const button = field.querySelector("[data-password-toggle]");
    const icon = button?.querySelector(".material-symbols-rounded");

    button?.addEventListener("click", () => {
        const shouldShow = input.type === "password";
        input.type = shouldShow ? "text" : "password";
        button.setAttribute("aria-label", shouldShow ? "Hide password" : "Show password");
        if (icon) {
            icon.textContent = shouldShow ? "visibility_off" : "visibility";
        }
    });
});

const cartDrawer = document.querySelector("[data-cart-drawer]");
document.querySelectorAll("[data-cart-open]").forEach((button) => {
    button.addEventListener("click", () => {
        cartDrawer?.classList.add("open");
    });
});

document.querySelectorAll("[data-cart-close]").forEach((button) => {
    button.addEventListener("click", () => {
        cartDrawer?.classList.remove("open");
    });
});

cartDrawer?.addEventListener("click", (event) => {
    if (event.target === cartDrawer) {
        cartDrawer.classList.remove("open");
    }
});

const observer = new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
        if (entry.isIntersecting) {
            entry.target.classList.add("visible");
            observer.unobserve(entry.target);
        }
    });
}, { threshold: 0.14 });

document.querySelectorAll(".reveal").forEach((element) => observer.observe(element));

document.querySelectorAll(".toast").forEach((toast) => {
    setTimeout(() => {
        toast.style.opacity = "0";
        toast.style.transform = "translateY(16px)";
    }, 2800);
});

document.querySelectorAll(".food-image-wrap img, .cart-image-wrap img").forEach((image) => {
    image.addEventListener("error", () => {
        image.closest(".food-image-wrap, .cart-image-wrap")?.classList.add("image-missing");
    });
});

const cravingData = {
    spicy: {
        title: "Paneer Tikka Bowl",
        meta: "Rs. 159 / 15 min / 4.9 rating",
        score: "96%"
    },
    budget: {
        title: "Maggi Tadka Bowl",
        meta: "Rs. 69 / 6 min / hostel classic",
        score: "91%"
    },
    group: {
        title: "Cheese Burst Pizza",
        meta: "Rs. 189 / 18 min / shareable",
        score: "88%"
    },
    sweet: {
        title: "Chocolate Waffle",
        meta: "Rs. 139 / 10 min / dessert pick",
        score: "94%"
    }
};

document.querySelectorAll("[data-craving-widget]").forEach((widget) => {
    const title = widget.querySelector("[data-craving-title]");
    const meta = widget.querySelector("[data-craving-meta]");
    const score = widget.querySelector("[data-craving-score]");
    const meter = widget.querySelector("[data-craving-meter]");
    const buttons = widget.querySelectorAll("[data-craving]");

    buttons.forEach((button) => {
        button.addEventListener("click", () => {
            const item = cravingData[button.dataset.craving];
            if (!item) {
                return;
            }
            buttons.forEach((option) => option.classList.remove("active"));
            button.classList.add("active");
            title.textContent = item.title;
            meta.textContent = item.meta;
            score.textContent = item.score;
            meter.style.width = item.score;
        });
    });
});

const categoryStudioData = {
    south: {
        label: "South Indian",
        title: "Crispy comfort between classes",
        text: "Dosa, idli and sambar picks for quick breakfast breaks and light lunch plans.",
        image: "/images/menu-photos/masala-dosa.png",
        alt: "Campus Masala Dosa",
        time: "9-12 min",
        rating: "4.8",
        price: "from Rs. 79",
        link: "/menu?category=SOUTH_INDIAN"
    },
    pizza: {
        label: "Pizza",
        title: "Shareable slices for group cravings",
        text: "Cheesy campus pizza for hostel nights, birthdays and project-team breaks.",
        image: "/images/menu-photos/cheese-pizza.png",
        alt: "Cheese Burst Pizza",
        time: "18 min",
        rating: "4.7",
        price: "from Rs. 189",
        link: "/menu?category=PIZZA"
    },
    meals: {
        label: "Meals",
        title: "Proper bowls when lunch gets serious",
        text: "Paneer bowls, rajma rice and filling plates that feel homemade but faster.",
        image: "/images/menu-photos/paneer-bowl.png",
        alt: "Paneer Tikka Bowl",
        time: "13-15 min",
        rating: "4.9",
        price: "from Rs. 119",
        link: "/menu?category=MEALS"
    },
    snacks: {
        label: "Snacks",
        title: "Fast bites for the five-minute gap",
        text: "Maggi, momos, fries and sandwiches built for quick cravings and budget wins.",
        image: "/images/menu-photos/maggi-noodles.png",
        alt: "Maggi Tadka Bowl",
        time: "6-11 min",
        rating: "4.6",
        price: "from Rs. 69",
        link: "/menu?category=SNACKS"
    },
    beverages: {
        label: "Beverages",
        title: "Cold sips and chai refills",
        text: "Coffee, chai and shakes for study sessions, morning starts and evening resets.",
        image: "/images/menu-photos/cold-coffee.png",
        alt: "Cold Coffee Cloud",
        time: "5-7 min",
        rating: "4.8",
        price: "from Rs. 49",
        link: "/menu?category=BEVERAGES"
    },
    desserts: {
        label: "Desserts",
        title: "Sweet finish after a long lecture",
        text: "Chocolate waffles and dessert picks with rich toppings and cafe-style plating.",
        image: "/images/menu-photos/chocolate-waffle.png",
        alt: "Chocolate Waffle",
        time: "10 min",
        rating: "4.9",
        price: "from Rs. 139",
        link: "/menu?category=DESSERTS"
    }
};

document.querySelectorAll("[data-category-studio]").forEach((studio) => {
    const image = studio.querySelector("[data-studio-image]");
    const label = studio.querySelector("[data-studio-label]");
    const title = studio.querySelector("[data-studio-title]");
    const text = studio.querySelector("[data-studio-text]");
    const time = studio.querySelector("[data-studio-time]");
    const rating = studio.querySelector("[data-studio-rating]");
    const price = studio.querySelector("[data-studio-price]");
    const link = studio.querySelector("[data-studio-link]");
    const card = studio.querySelector(".studio-card");

    studio.querySelectorAll("[data-studio-category]").forEach((button) => {
        button.addEventListener("click", () => {
            const data = categoryStudioData[button.dataset.studioCategory];
            if (!data) {
                return;
            }
            studio.querySelectorAll("[data-studio-category]").forEach((tab) => tab.classList.remove("active"));
            button.classList.add("active");
            card?.classList.remove("studio-pop");
            window.requestAnimationFrame(() => card?.classList.add("studio-pop"));
            image.src = data.image;
            image.alt = data.alt;
            label.textContent = data.label;
            title.textContent = data.title;
            text.textContent = data.text;
            time.textContent = data.time;
            rating.textContent = data.rating;
            price.textContent = data.price;
            link.href = data.link;
        });
    });
});
