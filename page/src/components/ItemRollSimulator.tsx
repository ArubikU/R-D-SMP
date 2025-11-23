import React, { useState } from 'react';
import { items } from '../data';

const getRarityColor = (rarity: string) => {
    switch(rarity.toLowerCase()) {
        case 'comun': return 'text-gray-400';
        case 'raro': return 'text-blue-400';
        case 'epico': return 'text-purple-400';
        case 'legendario': return 'text-yellow-400';
        case 'mitico': return 'text-red-500';
        default: return 'text-white';
    }
};

const getRarityLabel = (rarity: string) => {
    return rarity.charAt(0).toUpperCase() + rarity.slice(1);
};

export const ItemRollSimulator: React.FC = () => {
    const [simResult, setSimResult] = useState<any>(null);
    const [isSpinning, setIsSpinning] = useState(false);

    const spinRoulette = () => {
        if (isSpinning) return;
        setIsSpinning(true);
        setSimResult(null);
        
        let count = 0;
        const maxSpins = 20;
        const interval = setInterval(() => {
            // Visual spin - just pick random
            const randomItem = items[Math.floor(Math.random() * items.length)];
            setSimResult(randomItem);
            count++;
            if (count >= maxSpins) {
                clearInterval(interval);
                
                // Final result with weighted logic
                const roll = Math.random() * 100;
                let rarityPool = "comun";
                
                if (roll < 70) rarityPool = "comun";
                else if (roll < 90) rarityPool = "raro";
                else if (roll < 99) rarityPool = "epico";
                else rarityPool = "legendario"; // Includes mitico

                let pool = items.filter(i => i.rarity.toLowerCase() === rarityPool);
                if (rarityPool === "legendario") {
                     pool = items.filter(i => i.rarity.toLowerCase() === "legendario" || i.rarity.toLowerCase() === "mitico");
                }
                
                if (pool.length === 0) pool = items; // Fallback

                const finalItem = pool[Math.floor(Math.random() * pool.length)];
                setSimResult(finalItem);
                setIsSpinning(false);
            }
        }, 80);
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-[50vh] animate-in zoom-in duration-300">
            <div className="text-center mb-8">
                <h2 className="text-4xl text-white mb-2 text-shadow">SIMULADOR DE ITEM ROLL</h2>
                <p className="text-xl text-gray-400">Prueba tu suerte diaria. ¿Te saldrá basura o un tesoro?</p>
            </div>

            <div className="mc-card w-full max-w-md bg-black p-8 text-center min-h-[250px] flex items-center justify-center flex-col relative overflow-hidden ring-4 ring-blue-900/50">
                {/* Decoration lines */}
                <div className="absolute top-0 left-0 w-full h-2 bg-gradient-to-r from-blue-600 via-transparent to-blue-600"></div>
                <div className="absolute bottom-0 left-0 w-full h-2 bg-gradient-to-r from-blue-600 via-transparent to-blue-600"></div>
                <div className="absolute inset-0 bg-[url('https://www.transparenttextures.com/patterns/cubes.png')] opacity-10"></div>

                {simResult ? (
                    <div className="animate-in bounce-in duration-500 z-10">
                        <div className="text-6xl mb-4 filter drop-shadow-[0_0_10px_rgba(255,255,255,0.3)]">🎁</div>
                        <h3 className={`text-3xl mb-2 uppercase font-bold tracking-wider ${getRarityColor(simResult.rarity)}`}>
                            {simResult.name}
                        </h3>
                        <span className={`px-3 py-1 rounded-full border text-sm font-bold uppercase ${getRarityColor(simResult.rarity)} border-current opacity-80`}>
                            {getRarityLabel(simResult.rarity)}
                        </span>
                        <p className="mt-6 text-gray-300 text-xl border-t border-gray-800 pt-4">{simResult.desc}</p>
                    </div>
                ) : (
                    <div className="text-gray-600 text-2xl z-10">
                        {isSpinning ? "ABRIENDO..." : "ABRIR CAJA DIARIA"}
                    </div>
                )}
            </div>

            <button 
                onClick={spinRoulette} 
                disabled={isSpinning}
                className="mc-btn mt-8 px-8 py-4 text-2xl text-white font-bold uppercase tracking-widest hover:brightness-110 disabled:opacity-50 disabled:cursor-not-allowed transform hover:-translate-y-1 active:translate-y-0 transition-all bg-blue-900 border-blue-700"
            >
                {isSpinning ? "..." : "ROLL DIARIO"}
            </button>
            
            <div className="mt-8 grid grid-cols-4 gap-4 text-center text-sm text-gray-500">
                <div>
                    <div className="text-gray-400 font-bold">Común</div>
                    <div>70%</div>
                </div>
                <div>
                    <div className="text-blue-400 font-bold">Raro</div>
                    <div>20%</div>
                </div>
                <div>
                    <div className="text-purple-400 font-bold">Épico</div>
                    <div>9%</div>
                </div>
                <div>
                    <div className="text-yellow-400 font-bold">Leg/Mít</div>
                    <div>1%</div>
                </div>
            </div>
        </div>
    );
};
