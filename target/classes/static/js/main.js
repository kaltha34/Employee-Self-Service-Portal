// Main JavaScript for ESSP

document.addEventListener('DOMContentLoaded', function() {
    // Initialize date pickers for leave request forms
    const datePickers = document.querySelectorAll('.date-picker');
    if (datePickers.length > 0) {
        datePickers.forEach(function(picker) {
            picker.addEventListener('focus', function() {
                this.type = 'date';
            });
        });
    }

    // Form validation
    const forms = document.querySelectorAll('.needs-validation');
    if (forms.length > 0) {
        Array.from(forms).forEach(function(form) {
            form.addEventListener('submit', function(event) {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        });
    }

    // Calculate leave duration
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');
    const durationDisplay = document.getElementById('leaveDuration');

    if (startDateInput && endDateInput && durationDisplay) {
        const calculateDuration = function() {
            const startDate = new Date(startDateInput.value);
            const endDate = new Date(endDateInput.value);
            
            if (startDate && endDate && !isNaN(startDate) && !isNaN(endDate)) {
                // Calculate the difference in days
                const diffTime = Math.abs(endDate - startDate);
                const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1; // +1 to include both start and end dates
                
                durationDisplay.textContent = diffDays + (diffDays === 1 ? ' day' : ' days');
            } else {
                durationDisplay.textContent = '-';
            }
        };

        startDateInput.addEventListener('change', calculateDuration);
        endDateInput.addEventListener('change', calculateDuration);
    }

    // File input display
    const fileInput = document.querySelector('.custom-file-input');
    if (fileInput) {
        fileInput.addEventListener('change', function(e) {
            const fileName = e.target.files[0].name;
            const label = this.nextElementSibling;
            label.textContent = fileName;
        });
    }

    // Toggle password visibility
    const togglePassword = document.querySelector('.toggle-password');
    if (togglePassword) {
        togglePassword.addEventListener('click', function() {
            const passwordInput = document.querySelector(this.getAttribute('toggle'));
            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                this.textContent = 'Hide';
            } else {
                passwordInput.type = 'password';
                this.textContent = 'Show';
            }
        });
    }

    // Confirm dialog for actions
    const confirmButtons = document.querySelectorAll('[data-confirm]');
    if (confirmButtons.length > 0) {
        confirmButtons.forEach(function(button) {
            button.addEventListener('click', function(e) {
                if (!confirm(this.getAttribute('data-confirm'))) {
                    e.preventDefault();
                }
            });
        });
    }
});
