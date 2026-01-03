import { useNavigate } from "react-router-dom";

const HomePage = () => {
    const navigate = useNavigate();

    return (
        <div className="p-6">
            <h1 className="text-3xl font-bold">Välkommen till CMS!</h1>
            <p>Hantera dina kunder och användare på ett enkelt sätt.</p>

            <button className="bg-blue-500 text-white px-4 py-2 mt-4" onClick={() => navigate("/dashboard")}>
                Gå till Dashboard
            </button>

            <button className="bg-green-500 text-white px-4 py-2 mt-4 ml-4" onClick={() => navigate("/login")}>
                Logga in
            </button>
        </div>
    );
};

export default HomePage;
